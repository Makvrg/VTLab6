package ru.ifmo.se.dto.entity.fieldnames;

import lombok.Getter;

@Getter
public enum VehicleFieldNames {

    ID("id"),
    NAME("name"),
    X("x"),
    Y("y"),
    CREATION_DATE("creationDate"),
    ENGINE_POWER("enginePower"),
    DISTANCE_TRAVELLED("distanceTravelled"),
    TYPE("type"),
    FUEL_TYPE("fuelType"),
    USER_ID("userId");

    private final String title;

    VehicleFieldNames(String title) {
        this.title = title;
    }
}
