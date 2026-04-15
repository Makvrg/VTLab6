package ru.ifmo.se.repository;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.collection.CollectionWithInfo;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.service.exceptions.MaxEnginePowerNotExistException;

import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
public class DataRepository {

    private final CollectionWithInfo collectionWithInfo;
    private final DbRepository dbRepository;

    public Class<?> getCollectionType() {
        return collectionWithInfo.getCollectionType();
    }

    public Date getInitializationDate() {
        return collectionWithInfo.getInitializationDate();
    }

    public Class<?> getElementsType() {
        return collectionWithInfo.getElementsType();
    }

    public int getCountOfElements() {
        return collectionWithInfo.getCountOfElements();
    }

    public boolean existsUserByUsername(String username) throws SQLException {
        return dbRepository.existsUserByUsername(username);
    }

    public Optional<Long> findUserIdByUsername(String username) throws SQLException {
        return dbRepository.findUserIdByUsername(username);
    }

    public Optional<String> findUsernameById(Long id) throws SQLException {
        return dbRepository.findUsernameById(id);
    }

    public Optional<Long> add(Vehicle vehicle) throws SQLException {
        Optional<Long> newId = dbRepository.add(vehicle);
        if (newId.isPresent()) {
            vehicle.setId(newId.get());
            collectionWithInfo.getCollection().add(vehicle);
        }
        return newId;
    }

    public boolean addInitVehicle(Vehicle vehicle) {
        return collectionWithInfo.getCollection().add(vehicle);
    }

    public Optional<Long> add(User user) throws SQLException {
        return dbRepository.add(user);
    }

    public Optional<Vehicle> findMinVehicle() {
        if (collectionWithInfo.getCountOfElements() > 0) {
            return Optional.of(
                    Collections.min(collectionWithInfo.getCollection())
            );
        }
        return Optional.empty();
    }

    public double findMaxEnginePower() {
        if (collectionWithInfo.getCountOfElements() > 0) {
            return collectionWithInfo.getCollection().stream()
                    .mapToDouble(Vehicle::getEnginePower)
                    .max()
                    .orElseThrow(MaxEnginePowerNotExistException::new);
        }
        throw new MaxEnginePowerNotExistException();
    }

    public Optional<Vehicle> findVehicleByEnginePower(double enginePower) {
        for (Vehicle vehicle : collectionWithInfo.getCollection()) {
            if (vehicle.getEnginePower() == enginePower) {
                return Optional.of(vehicle);
            }
        }
        return Optional.empty();
    }

    public boolean updateById(long vehicleId, Vehicle newData, Long userId) throws SQLException {
        if (dbRepository.updateById(newData, vehicleId, userId)) {
            for (Vehicle vehicle : collectionWithInfo.getCollection()) {
                if (vehicle.getId() == vehicleId && vehicle.getUserId().equals(userId)) {
                    updateVehicle(vehicle, newData);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void updateVehicle(Vehicle targetVehicle, Vehicle newData) {
        targetVehicle.setName(newData.getName());
        targetVehicle.setCoordinates(newData.getCoordinates());
        targetVehicle.setEnginePower(newData.getEnginePower());
        targetVehicle.setDistanceTravelled(newData.getDistanceTravelled());
        targetVehicle.setType(newData.getType());
        targetVehicle.setFuelType(newData.getFuelType());
    }

    public Collection<Vehicle> findAllVehicles() {
        return collectionWithInfo.getCollection();
    }

    public Collection<Vehicle> findAllVehiclesFromDb() throws SQLException {
        return dbRepository.findAllVehicles();
    }

    public Collection<User> findAllUsers() throws SQLException {
        return dbRepository.findAllUsers();
    }

    public boolean softDeleteVehicleById(long vehicleId, Long userId) throws SQLException {
        if (dbRepository.softDeleteVehicleById(vehicleId, userId)) {
            for (Iterator<Vehicle> itr = collectionWithInfo.getCollection()
                    .iterator(); itr.hasNext(); ) {
                Vehicle currentVeh = itr.next();
                if (currentVeh.getId() == vehicleId && currentVeh.getUserId().equals(userId)) {
                    itr.remove();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean softDeleteVehicles(Long userId) throws SQLException {
        if (dbRepository.softDeleteVehicles(userId)) {
            for (Iterator<Vehicle> itr = collectionWithInfo.getCollection()
                    .iterator(); itr.hasNext(); ) {
                Vehicle currentVeh = itr.next();
                if (currentVeh.getUserId().equals(userId)) {
                    itr.remove();
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public boolean truncateVehicles() throws SQLException {
        if (dbRepository.truncateVehicles()) {
            collectionWithInfo.getCollection().clear();
            return true;
        }
        return false;
    }

    public Map<Float, List<Vehicle>> groupByDistanceTravelled() {
        Map<Float, List<Vehicle>> groups = new HashMap<>();
        for (Vehicle vehicle : collectionWithInfo.getCollection()) {
            if (!groups.containsKey(vehicle.getDistanceTravelled())) {
                groups.put(
                        vehicle.getDistanceTravelled(), new ArrayList<>(List.of(vehicle))
                );
            } else {
                groups.get(vehicle.getDistanceTravelled()).add(vehicle);
            }
        }
        return groups;
    }
}
