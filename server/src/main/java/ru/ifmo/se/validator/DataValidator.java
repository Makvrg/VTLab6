package ru.ifmo.se.validator;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.entity.VehicleTypeDto;
import ru.ifmo.se.entity.VehicleType;
import ru.ifmo.se.validator.exceptions.CountLessThanTypeValidationException;
import ru.ifmo.se.validator.exceptions.InputFieldValidationException;
import ru.ifmo.se.validator.exceptions.RemoveByIdValidationException;
import ru.ifmo.se.validator.exceptions.VehicleDtoValidationException;

public class DataValidator {

    public void validateVehicleDto(VehicleDto vehicleDto) {
        try {
            validateTypedEnginePower(vehicleDto.getEnginePower());
            validateTypedDistanceTravelled(vehicleDto.getDistanceTravelled());
        } catch (InputFieldValidationException e) {
            throw new VehicleDtoValidationException(e.getMessage());
        }
    }

    public void validateTypedEnginePower(double pow) {
        if (Double.isNaN(pow)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ENGINE_POWER_MUST_BE_REAL_NUM);
        }
        if (Double.isInfinite(pow)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ABS_ENGINE_POWER_MUST_BE_LESS_MAX);
        }
    }

    public void validateTypedDistanceTravelled(Float dist) {
        if (dist == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_NOT_NULL);
        }
        if (Float.isNaN(dist)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_REAL_NUM);
        }
        if (Float.isInfinite(dist)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ABS_DISTANCE_TRAVELLED_MUST_BE_LESS_MAX);
        }
    }

    public void validateRemoveById(Long id) {
        if (id == null) {
            throw new RemoveByIdValidationException(
                    ValidatorMessages.PARAMETER_ID_NOT_PASSED);
        }
        if (id < 1) {
            throw new RemoveByIdValidationException(
                    ValidatorMessages.ARGUMENT_ID_MUST_BE_MORE_ZERO
            );
        }
        }

    public void validateCountLessType(VehicleTypeDto vehicleTypeDto) {
        if (vehicleTypeDto == null) {
            throw new CountLessThanTypeValidationException(
                    ValidatorMessages.PARAMETER_TYPE_NOT_PASSED);
        }
        try {
            VehicleType.valueOf(vehicleTypeDto.name());
        } catch (IllegalArgumentException e) {
            throw new CountLessThanTypeValidationException(
                    ValidatorMessages.PARAMETER_TYPE_INCORRECT);
        }
    }
}
