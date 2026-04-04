package ru.ifmo.se.service;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.dto.supporting.CollectionInfoDto;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.entity.VehicleType;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.init.DbMigrator;
import ru.ifmo.se.passwordhasher.PasswordHasher;
import ru.ifmo.se.repository.DataRepository;
import ru.ifmo.se.service.exceptions.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
public class CollectionService {

    private final DataRepository dataRepository;
    private final DbConnectionManager connectionManager;
    private final DbMigrator migrator;
    private final List<ShutdownListener> listeners = new ArrayList<>();

    public boolean exit() {
        shutdown();
        return true;
    }

    public boolean registration(User newUser, String rawPassword) {
        try {
            connectionManager.beginTransaction(8);

            for (User user : dataRepository.findAllUsers()) {
                if (newUser.getUsername().equals(user.getUsername())) {
                    return false;
                }
            }

            newUser.setId(createNewUserId());
            String salt = PasswordHasher.generateSalt();
            newUser.setSalt(salt);
            try {
                newUser.setHashedPassword(PasswordHasher.hashPassword(rawPassword, salt));
            } catch (NoSuchAlgorithmException e) {
                throw new NoSuchAlgorithmRuntimeException("Не найден алгоритм хеширования" +
                        e.getMessage());
            }
            return dataRepository.add(newUser);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public boolean auth(User enteredUser, String rawPassword) {
        try {
            connectionManager.beginTransaction(8);

            for (User user: dataRepository.findAllUsers()) {
                if (enteredUser.getUsername().equals(user.getUsername())) {
                    try {
                        return PasswordHasher.verifyPassword(
                                rawPassword, user.getSalt(), user.getHashedPassword());
                    } catch (NoSuchAlgorithmException e) {
                        throw new NoSuchAlgorithmRuntimeException(
                                "Не найден алгоритм хеширования" + e.getMessage());
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public CollectionInfoDto info() {
        synchronized (dataRepository.findAllVehicles()) {
            return new CollectionInfoDto(
                    dataRepository.getCollectionType().getSimpleName(),
                    dataRepository.getInitializationDate(),
                    dataRepository.getElementsType().getSimpleName(),
                    dataRepository.getCountOfElements()
            );
        }
    }

    public boolean add(Vehicle vehicle, String username) {
        try {
            connectionManager.beginTransaction(8);
            vehicle.setId(createNewVehicleId());
            vehicle.setCreationDate(new Date());
            Optional<Long> userId = dataRepository.findUserIdByUsername(username);
            if (userId.isPresent()) {
                vehicle.setUserId(userId.get());
            } else {
                return false;
            }
            return dataRepository.add(vehicle);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public boolean addInitVehicle(Vehicle vehicle) {
        try {
            connectionManager.beginTransaction(8);
            if (vehicle.getCreationDate().after(new Date())) {
                throw new CreationDateIsAfterNowException(
                        "Передана дата и время создания объекта Vehicle из будущего");
            }
            return dataRepository.add(vehicle);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    private long createNewVehicleId() {
        try {
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.findVehicleMaxId() + 1L;
            }
        } catch (MaxIdNotExistException e) {
            return 1L;
        }
    }

    private long createNewUserId() throws SQLException {
        try {
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.findUserMaxId() + 1L;
            }
        } catch (MaxIdNotExistException e) {
            return 1L;
        }
    }

    public Optional<String> findUsernameById(Long id) {
        try {
            connectionManager.beginTransaction(4);
            return dataRepository.findUsernameById(id);
        } catch (SQLException e) {
        throw new SQLRuntimeException(e.getMessage());
    } finally {
        try {
            connectionManager.commit();
            connectionManager.close();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
        }
    }

    public boolean addIfMin(Vehicle vehicle, String username) {
        synchronized (dataRepository.findAllVehicles()) {
            Optional<Vehicle> minVehicle = dataRepository.findMinVehicle();
            if (minVehicle.isPresent() && vehicle.compareTo(minVehicle.get()) < 0) {
                return add(vehicle, username);
            } else if (minVehicle.isEmpty()) {
                return add(vehicle, username);
            } else {
                return false;
            }
        }
    }

    public boolean updateById(Vehicle vehicle, Long id) {
        try {
            connectionManager.beginTransaction(4);
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.updateById(id, vehicle);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public Collection<Vehicle> showVehicles() {
        return dataRepository.findAllVehicles();
    }

    public boolean removeVehicleById(long id) {
        try {
            connectionManager.beginTransaction(4);
            try {
                synchronized (dataRepository.findAllVehicles()) {
                    return dataRepository.deleteVehicleById(id);
                }
            } catch (IllegalStateException e) {
                throw new RemoveByIdIllegalStateException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public boolean clearVehicles() {
        try {
            connectionManager.beginTransaction(8);
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.deleteAllVehicles();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public void useMigrate() {
        migrator.migrate(connectionManager.getDataSource());
    }

    public Collection<Vehicle> findAllVehiclesFromDb() {
        try {
            connectionManager.beginTransaction(8);
            return dataRepository.findAllVehiclesFromDb();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        } finally {
            try {
                connectionManager.commit();
                connectionManager.close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e.getMessage());
            }
        }
    }

    public boolean removeGreater(Vehicle enteredVehicle) {
        synchronized (dataRepository.findAllVehicles()) {
            boolean returned = false;
            for (Vehicle vehicle : dataRepository.findAllVehicles()) {
                if (vehicle.compareTo(enteredVehicle) > 0) {
                    removeVehicleById(vehicle.getId());
                    returned = true;
                }
            }
            return returned;
        }
    }

    public boolean removeLower(Vehicle enteredVehicle) {
        synchronized (dataRepository.findAllVehicles()) {
            boolean returned = false;
            for (Vehicle vehicle : dataRepository.findAllVehicles()) {
                if (vehicle.compareTo(enteredVehicle) < 0) {
                    removeVehicleById(vehicle.getId());
                    returned = true;
                }
            }
            return returned;
        }
    }

    public Optional<Vehicle> maxByEnginePower() {
        synchronized (dataRepository.findAllVehicles()) {
            double maxEnginePower;
            try {
                maxEnginePower = dataRepository.findMaxEnginePower();
            } catch (MaxEnginePowerNotExistException e) {
                return Optional.empty();
            }
            return dataRepository.findVehicleByEnginePower(maxEnginePower);
        }
    }

    public Map<Float, Integer> groupCountingByDistanceTravelled() {
        synchronized (dataRepository.findAllVehicles()) {
            Map<Float, List<Vehicle>> groups = dataRepository.groupByDistanceTravelled();
            HashMap<Float, Integer> counts = new HashMap<>();
            for (Map.Entry<Float, List<Vehicle>> entry : groups.entrySet()) {
                counts.put(entry.getKey(), entry.getValue().size());
            }
            return counts;
        }
    }

    public long countLessThanType(VehicleType vehicleType) {
        synchronized (dataRepository.findAllVehicles()) {
            return dataRepository.findAllVehicles().stream()
                    .filter(vehicle -> vehicle.getType().compareTo(vehicleType) < 0)
                    .count();
        }
    }

    public int getCountElementsCollection() {
        return dataRepository.getCountOfElements();
    }

    public void addShutdownListener(ShutdownListener listener) {
        listeners.add(listener);
    }

    private void shutdown() {
        listeners.forEach(ShutdownListener::onShutdown);
    }
}
