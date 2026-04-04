package ru.ifmo.se.dto.entity.fieldnames;

import lombok.Getter;

@Getter
public enum VehicleDtoFieldNames {

    ID("id"),
    NAME("name"),
    X("x"),
    Y("y"),
    CREATION_DATE("creationDate"),
    ENGINE_POWER("enginePower"),
    DISTANCE_TRAVELLED("distanceTravelled"),
    TYPE("type"),
    FUEL_TYPE("fuelType"),
    USERNAME("username");

    private final String title;

    VehicleDtoFieldNames(String title) {
        this.title = title;
    }
}
