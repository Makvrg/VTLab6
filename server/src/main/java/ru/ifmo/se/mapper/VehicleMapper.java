package ru.ifmo.se.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.entity.Vehicle;

@Mapper(uses = {CoordinatesMapper.class})
public interface VehicleMapper {
    VehicleMapper MAPPER = Mappers.getMapper(VehicleMapper.class);

    VehicleDto toDto(Vehicle vehicle);

    Vehicle toEntity(VehicleDto vehicleDto);
}
