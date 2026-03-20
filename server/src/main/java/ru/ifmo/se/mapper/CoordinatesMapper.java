package ru.ifmo.se.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ifmo.se.dto.entity.CoordinatesDto;
import ru.ifmo.se.entity.Coordinates;

@Mapper
public interface CoordinatesMapper {

    CoordinatesMapper MAPPER = Mappers.getMapper(CoordinatesMapper.class);

    CoordinatesDto toDto(Coordinates coordinates);

    Coordinates toEntity(CoordinatesDto coordinatesDto);
}
