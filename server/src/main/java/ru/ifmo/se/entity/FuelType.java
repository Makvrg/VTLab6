package ru.ifmo.se.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FuelType {

    ELECTRICITY("Электричество"),
    NUCLEAR("Ядерная энергия"),
    PLASMA("Плазма");

    private final String title;

    public static FuelType fromRussianString(String russianName) {
        if (russianName == null) {
            throw new IllegalArgumentException("Передано пустое значение");
        }
        for (FuelType vehicleType : FuelType.values()) {
            if (vehicleType.title.equals(russianName)) {
                return vehicleType;
            }
        }
        throw new IllegalArgumentException("Неизвестное значение: " + russianName);
    }

    public static boolean containsRussianString(String russianName) {
        if (russianName == null) {
            throw new IllegalArgumentException("Передано пустое значение");
        }
        return Arrays.stream(FuelType.values())
                .map(FuelType::getTitle)
                .toList()
                .contains(russianName);
    }

    @Override
    public String toString() {
        return title;
    }
}
