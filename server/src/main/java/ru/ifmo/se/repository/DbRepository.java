package ru.ifmo.se.repository;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class DbRepository {

    private final DbConnectionManager connectionManager;

    public boolean add(User user) throws SQLException {
        String sql = """
                INSERT INTO "user" (user_id, username, hashed_password, salt)
                    VALUES (?, ?, ?, ?)
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getHashedPassword());
            preparedStatement.setString(4, user.getSalt());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean add(Vehicle vehicle) throws SQLException {
        String sql = """
                INSERT INTO vehicle (vehicle_id, name, x, y, creation_date,
                    engine_power, distance_travelled, type, fuel_type, user_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, vehicle.getId());
            preparedStatement.setString(2, vehicle.getName());
            preparedStatement.setInt(3, vehicle.getCoordinates().getX());
            preparedStatement.setLong(4, vehicle.getCoordinates().getY());
            preparedStatement.setDate(5, new java.sql.Date(vehicle.getCreationDate().getTime()));
            preparedStatement.setDouble(6, vehicle.getEnginePower());
            preparedStatement.setFloat(7, vehicle.getDistanceTravelled());
            preparedStatement.setString(8, vehicle.getType().name());
            preparedStatement.setString(9, vehicle.getFuelType().name());
            preparedStatement.setLong(10, vehicle.getUserId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean updateById(Vehicle vehicle, Long id) throws SQLException {
        String sql = """
                UPDATE vehicle SET name = ?, x = ?, y = ?, engine_power = ?,
                    distance_travelled = ?, type = ?, fuel_type = ?
                        WHERE vehicle_id = ?
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
            preparedStatement.setLong(8, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteVehicleById(Long id) throws SQLException {
        String sql = """
                DELETE FROM vehicle
                    WHERE vehicle_id = ?
                """;
        Connection connection = connectionManager.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

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
                    WHERE username = ?
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
                    WHERE user_id = ?
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
        String sql = "SELECT * FROM vehicle";
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
                                    resultSet.getDate("creation_date"),
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
        String sql = "SELECT * FROM \"user\"";
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
