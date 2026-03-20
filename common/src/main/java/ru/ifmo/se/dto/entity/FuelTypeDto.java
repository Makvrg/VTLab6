package ru.ifmo.se.dto.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public enum FuelTypeDto implements Serializable {

    ELECTRICITY,
    NUCLEAR,
    PLASMA
}
