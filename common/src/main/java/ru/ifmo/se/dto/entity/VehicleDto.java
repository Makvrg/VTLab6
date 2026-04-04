package ru.ifmo.se.dto.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.entity.fieldmessages.FieldMessages;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto implements Serializable {

    private long id;

    @NotBlank(message = FieldMessages.NAME_MUST_BE_NON_BLANK)
    private String name;

    @NotNull(message = FieldMessages.COORDS_MUST_BE_NOT_NULL)
    @Valid
    private CoordinatesDto coordinates;

    private Date creationDate;

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = FieldMessages.ENGINE_POWER_MUST_BE_MORE_ZERO
    )
    private double enginePower;

    @NotNull(message = FieldMessages.DISTANCE_TRAVELLED_MUST_BE_NOT_NULL)
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = FieldMessages.DISTANCE_TRAVELLED_MUST_BE_MORE_ZERO
    )
    private Float distanceTravelled;

    @NotNull(message = FieldMessages.VEHICLE_TYPE_MUST_BE_NOT_NULL)
    private VehicleTypeDto type;

    @NotNull(message = FieldMessages.FUEL_TYPE_MUST_BE_NOT_NULL)
    private FuelTypeDto fuelType;

    private String username;
}
