package ru.ifmo.se.mapper;

import ru.ifmo.se.dto.entity.CoordinatesDto;
import ru.ifmo.se.dto.entity.FuelTypeDto;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.entity.VehicleTypeDto;
import ru.ifmo.se.entity.Coordinates;
import ru.ifmo.se.entity.FuelType;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.entity.VehicleType;

public class VehicleMapper {

    public static VehicleDto toDto(Vehicle veh, String username) {
        return new VehicleDto(veh.getId(), veh.getName(),
                new CoordinatesDto(
                        veh.getCoordinates().getX(),
                        veh.getCoordinates().getY()
                ), veh.getCreationDate(), veh.getEnginePower(),
                veh.getDistanceTravelled(), VehicleTypeDto.valueOf(veh.getType().name()),
                FuelTypeDto.valueOf(veh.getFuelType().name()), username
        );
    }

    public static Vehicle toEntity(VehicleDto veh, Long userId) {
        return new Vehicle(veh.getId(), veh.getName(),
                new Coordinates(
                        veh.getCoordinates().getX(),
                        veh.getCoordinates().getY()
                ), veh.getCreationDate(), veh.getEnginePower(),
                veh.getDistanceTravelled(), VehicleType.valueOf(veh.getType().name()),
                FuelType.valueOf(veh.getFuelType().name()), userId
        );
    }
}
