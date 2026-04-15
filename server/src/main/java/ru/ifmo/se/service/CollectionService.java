package ru.ifmo.se.service;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.dto.supporting.CollectionInfoDto;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.entity.VehicleType;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.repository.init.DbMigrator;
import ru.ifmo.se.passwordhasher.PasswordHasher;
import ru.ifmo.se.repository.DataRepository;
import ru.ifmo.se.service.exceptions.*;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
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
        return executeInTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                () -> {
                    if (dataRepository.existsUserByUsername(newUser.getUsername())) {
                        return false;
                    }

                    String salt = PasswordHasher.generateSalt();
                    newUser.setSalt(salt);
                    try {
                        newUser.setHashedPassword(PasswordHasher.hashPassword(rawPassword, salt));
                    } catch (NoSuchAlgorithmException e) {
                        throw new NoSuchAlgorithmRuntimeException("Не найден алгоритм хеширования" +
                                e.getMessage());
                    }
                    return dataRepository.add(newUser).isPresent();
                }
        );
    }

    public boolean auth(User enteredUser, String rawPassword) {
        try {
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
        return executeInTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                () -> {
                    try {
                        Optional<Long> userId = dataRepository.findUserIdByUsername(username);
                        if (userId.isPresent()) {
                            vehicle.setUserId(userId.get());
                        } else {
                            return false;
                        }
                        vehicle.setCreationDate(new Date());
                        return dataRepository.add(vehicle).isPresent();
                    } catch (SQLException e) {
                        throw new SQLRuntimeException(e.getMessage());
                    }
                }
        );
    }

    public boolean addInitVehicle(Vehicle vehicle) {
        if (vehicle.getCreationDate().after(new Date())) {
            throw new CreationDateIsAfterNowException(
                    "Передана дата и время создания объекта Vehicle из будущего");
        }
        synchronized (dataRepository.findAllVehicles()) {
            return dataRepository.addInitVehicle(vehicle);
        }
    }

    public Optional<String> findUsernameById(Long id) {
        try {
            return dataRepository.findUsernameById(id);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public boolean addIfMin(Vehicle vehicle, String username) {
        synchronized (dataRepository.findAllVehicles()) {
            Optional<Vehicle> minVehicle = dataRepository.findMinVehicle();
            if (minVehicle.isPresent() && vehicle.compareTo(minVehicle.get()) < 0 ||
                    minVehicle.isEmpty()) {
                return add(vehicle, username);
            } else {
                return false;
            }
        }
    }

    public boolean updateById(Vehicle newData, Long vehicleId, String username) {
        try {
            Optional<Long> userId = dataRepository.findUserIdByUsername(username);
            if (userId.isEmpty()) {
                return false;
            }
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.updateById(vehicleId, newData, userId.get());
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public Collection<Vehicle> showVehicles() {
        return dataRepository.findAllVehicles();
    }

    public boolean removeVehicleById(long vehicleId, String username) {
        return executeInTransaction(
                Connection.TRANSACTION_READ_COMMITTED,
                () -> {
                    Optional<Long> userId = dataRepository.findUserIdByUsername(username);
                    if (userId.isEmpty()) {
                        return false;
                    }
                    try {
                        synchronized (dataRepository.findAllVehicles()) {
                            return dataRepository.softDeleteVehicleById(vehicleId, userId.get());
                        }
                    } catch (IllegalStateException e) {
                        throw new RemoveByIdIllegalStateException(e.getMessage());
                    }
                }
        );
    }

    public boolean clearVehicles(String username) {
        return executeInTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                () -> {
                    Optional<Long> userId = dataRepository.findUserIdByUsername(username);
                    if (userId.isEmpty()) {
                        return false;
                    }
                    try {
                        synchronized (dataRepository.findAllVehicles()) {
                            return dataRepository.softDeleteVehicles(userId.get());
                        }
                    } catch (IllegalStateException e) {
                        throw new RemoveByIdIllegalStateException(e.getMessage());
                    }
                }
        );
    }

    public boolean clearVehiclesDb() {
        try {
            synchronized (dataRepository.findAllVehicles()) {
                return dataRepository.truncateVehicles();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public void useMigrate() {
        try {
            migrator.migrate(connectionManager.getConnection());
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public Collection<Vehicle> findAllVehiclesFromDb() {
        try {
            return dataRepository.findAllVehiclesFromDb();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e.getMessage());
        }
    }

    public boolean removeGreater(Vehicle enteredVehicle, String username) {
        synchronized (dataRepository.findAllVehicles()) {
            boolean returned = false;
            for (Vehicle vehicle : dataRepository.findAllVehicles()) {
                if (vehicle.compareTo(enteredVehicle) > 0) {
                    removeVehicleById(vehicle.getId(), username);
                    returned = true;
                }
            }
            return returned;
        }
    }

    public boolean removeLower(Vehicle enteredVehicle, String username) {
        synchronized (dataRepository.findAllVehicles()) {
            boolean returned = false;
            for (Vehicle vehicle : dataRepository.findAllVehicles()) {
                if (vehicle.compareTo(enteredVehicle) < 0) {
                    removeVehicleById(vehicle.getId(), username);
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

    private <T> T executeInTransaction(int isolationLevel, TransactionalOperation<T> operation) {
        try {
            connectionManager.beginTransaction(isolationLevel);
            T result = operation.execute();
            connectionManager.commit();
            return result;
        } catch (SQLException e) {
            try {
                connectionManager.rollback();
            } catch (SQLException rollbackEx) {
                throw new SQLRuntimeException(e.getMessage() +
                        " and Rollback failed " + rollbackEx.getMessage());
            }
            throw new SQLRuntimeException(e.getMessage());
        } catch (RuntimeException e) {
            try {
                connectionManager.rollback();
            } catch (SQLException rollbackEx) {
                throw e;
            }
            throw e;
        }
    }

    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute() throws SQLException;
    }
}
