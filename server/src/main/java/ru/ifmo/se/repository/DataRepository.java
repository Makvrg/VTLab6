package ru.ifmo.se.repository;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.collection.CollectionWithInfo;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.service.exceptions.MaxEnginePowerNotExistException;
import ru.ifmo.se.service.exceptions.MaxIdNotExistException;

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

    public long findVehicleMaxId() {
        if (collectionWithInfo.getCountOfElements() > 0) {
            return collectionWithInfo.getCollection().stream()
                    .mapToLong(Vehicle::getId)
                    .max()
                    .orElseThrow(MaxIdNotExistException::new);
        }
        throw new MaxIdNotExistException();
    }

    public long findUserMaxId() {
        return findAllUsers().stream()
                .mapToLong(User::getId)
                .max()
                .orElseThrow(MaxIdNotExistException::new);
    }

    public Optional<Long> findUserIdByUsername(String username) {
        return dbRepository.findUserIdByUsername(username);
    }

    public boolean add(Vehicle vehicle) {
        if (dbRepository.add(vehicle)) {
            return collectionWithInfo.getCollection().add(vehicle);
        }
        return false;
    }

    public boolean add(User user) {
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

    public boolean updateById(long id, Vehicle newData) {
        if (dbRepository.updateById(newData, id)) {
            for (Vehicle vehicle : collectionWithInfo.getCollection()) {
                if (vehicle.getId() == id) {
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

    public Collection<Vehicle> findAllVehiclesFromDb() {
        return dbRepository.findAllVehicle();
    }

    public Collection<User> findAllUsers() {
        return dbRepository.findAllUser();
    }

    public boolean deleteVehicleById(long id) {
        if (dbRepository.deleteVehicleById(id)) {
            for (Iterator<Vehicle> itr = collectionWithInfo.getCollection().iterator(); itr.hasNext(); ) {
                if (itr.next().getId() == id) {
                    itr.remove();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean deleteAllVehicles() {
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
