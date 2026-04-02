package ru.ifmo.se.repository;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class DbRepository {

    private final DbConnectionManager connectionManager;

    public boolean add(User user) {
    }

    public boolean add(Vehicle vehicle) {
    }

    public boolean updateById(Vehicle vehicle, Long id) {
    }

    public boolean deleteVehicleById(Long id) {
    }

    public boolean truncateVehicles() {
    }

    public Optional<Long> findUserIdByUsername(String username) {
    }

    public Collection<Vehicle> findAllVehicle() {
    }

    public Collection<User> findAllUser() {
    }
}
