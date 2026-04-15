package ru.ifmo.se.repository;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class DbRepository {

    private final DbConnectionManager connectionManager;

    public boolean existsUserByUsername(String username) throws SQLException {
        String sql = """
                SELECT EXISTS (SELECT 1 FROM "user"
                                    WHERE is_deleted = false AND
                                          username = ?) AS flag
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("flag");
                } else {
                    return false;
                }
            }
        }
    }

    public Optional<Long> add(User user) throws SQLException {
        String sql = """
                INSERT INTO "user" (username, hashed_password, salt, is_deleted)
                    VALUES (?, ?, ?, false)
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getHashedPassword());
            preparedStatement.setString(3, user.getSalt());

            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong(1));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Long> add(Vehicle vehicle) throws SQLException {
        String sql = """
                INSERT INTO vehicle (name, x, y, creation_date,
                    engine_power, distance_travelled, type, fuel_type, user_id, is_deleted)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, false)
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, vehicle.getName());
            preparedStatement.setInt(2, vehicle.getCoordinates().getX());
            preparedStatement.setLong(3, vehicle.getCoordinates().getY());
            preparedStatement.setTimestamp(4, new Timestamp(vehicle.getCreationDate().getTime()));
            preparedStatement.setDouble(5, vehicle.getEnginePower());
            preparedStatement.setFloat(6, vehicle.getDistanceTravelled());
            preparedStatement.setString(7, vehicle.getType().name());
            preparedStatement.setString(8, vehicle.getFuelType().name());
            preparedStatement.setLong(9, vehicle.getUserId());

            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong(1));
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateById(Vehicle vehicle, Long vehicleId, Long userId) throws SQLException {
        String sql = """
                UPDATE vehicle SET name = ?, x = ?, y = ?, engine_power = ?,
                    distance_travelled = ?, type = ?, fuel_type = ?
                        WHERE is_deleted = false AND
                              vehicle_id = ? AND
                              user_id = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicle.getName());
            preparedStatement.setInt(2, vehicle.getCoordinates().getX());
            preparedStatement.setLong(3, vehicle.getCoordinates().getY());
            preparedStatement.setDouble(4, vehicle.getEnginePower());
            preparedStatement.setFloat(5, vehicle.getDistanceTravelled());
            preparedStatement.setString(6, vehicle.getType().name());
            preparedStatement.setString(7, vehicle.getFuelType().name());
            preparedStatement.setLong(8, vehicleId);
            preparedStatement.setLong(9, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean softDeleteVehicleById(Long vehicleId, Long userId) throws SQLException {
        String sql = """
                UPDATE vehicle SET is_deleted = true
                    WHERE is_deleted = false AND
                          vehicle_id = ? AND
                          user_id = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, vehicleId);
            preparedStatement.setLong(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean softDeleteVehicles(Long userId) throws SQLException {
        String sql = """
                UPDATE vehicle SET is_deleted = true
                    WHERE is_deleted = false AND
                          user_id = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean truncateVehicles() throws SQLException {
        String sql = "TRUNCATE TABLE vehicle";
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Optional<Long> findUserIdByUsername(String username) throws SQLException {
        String sql = """
                SELECT user_id FROM "user"
                    WHERE is_deleted = false AND
                          username = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getLong("user_id"));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Optional<String> findUsernameById(Long id) throws SQLException {
        String sql = """
                SELECT username FROM "user"
                    WHERE is_deleted = false AND
                          user_id = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getString("username"));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Collection<Vehicle> findAllVehicles() throws SQLException {
        String sql = """
                SELECT * FROM vehicle
                    WHERE is_deleted = false
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Collection<Vehicle> vehicles = new ArrayList<>();
                while (resultSet.next()) {
                    VehicleType type;
                    try {
                        type = VehicleType.valueOf(resultSet.getString("type"));
                    } catch (IllegalArgumentException e) {
                        type = null;
                    }
                    FuelType fuelType;
                    try {
                        fuelType = FuelType.valueOf(resultSet.getString("fuel_type"));
                    } catch (IllegalArgumentException e) {
                        fuelType = null;
                    }
                    vehicles.add(
                            new Vehicle(
                                    resultSet.getLong("vehicle_id"),
                                    resultSet.getString("name"),
                                    new Coordinates(
                                            resultSet.getInt("x"),
                                            resultSet.getLong("y")
                                    ),
                                    resultSet.getTimestamp("creation_date"),
                                    resultSet.getDouble("engine_power"),
                                    resultSet.getFloat("distance_travelled"),
                                    type,
                                    fuelType,
                                    resultSet.getLong("user_id")
                            )
                    );
                }
                return vehicles;
            }
        }
    }

    public Collection<User> findAllUsers() throws SQLException {
        String sql = """
                SELECT * FROM "user"
                    WHERE is_deleted = false
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Collection<User> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(
                            new User(
                                    resultSet.getLong("user_id"),
                                    resultSet.getString("username"),
                                    resultSet.getString("hashed_password"),
                                    resultSet.getString("salt")
                            )
                    );
                }
                return users;
            }
        }
    }
}
