package ru.ifmo.se.validator;

import ru.ifmo.se.dto.entity.FuelTypeDto;
import ru.ifmo.se.dto.entity.VehicleTypeDto;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.output.translator.Translator;
import ru.ifmo.se.validator.exceptions.InputFieldValidationException;

public class DataValidator {

    public void validateXCoordType(String x) {
        if (x != null) {
            try {
                Integer.parseInt(x);
            } catch (NumberFormatException e) {
                throw new InputFieldValidationException(
                        ValidatorMessages.X_COORD_MUST_BE_INTEGER);
            }
        }
    }

    public void validateYCoordType(String y) {
        if (y == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.Y_COORD_MUST_BE_NOT_EMPTY);
        }
        try {
            Long.parseLong(y);
        } catch (NumberFormatException e) {
            throw new InputFieldValidationException(
                    ValidatorMessages.Y_COORD_MUST_BE_INTEGER);
        }
    }

    public void validateEnginePowerType(String enginePower) {
        if (enginePower == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ENGINE_POWER_MUST_BE_NOT_EMPTY);
        }
        double pow;
        try {
            pow = Double.parseDouble(enginePower);
        } catch (NumberFormatException e) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ENGINE_POWER_MUST_BE_REAL_NUM);
        }
        if (Double.isNaN(pow)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ENGINE_POWER_MUST_BE_REAL_NUM);
        }
        if (Double.isInfinite(pow)) {
            throw new InputFieldValidationException(
                    ValidatorMessages.ABS_ENGINE_POWER_MUST_BE_LESS_MAX);
        }
    }

    public void validateDistanceTravelledType(String distanceTravelled) {
        if (distanceTravelled == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_NOT_NULL);
        }
        float dist;
        try {
            dist = Float.parseFloat(distanceTravelled);
        } catch (NumberFormatException e) {
            throw new InputFieldValidationException(
                    ValidatorMessages.DISTANCE_TRAVELLED_MUST_BE_REAL_NUM);
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

    public void validateRusVehicleType(String rusVehicleType) {
        if (rusVehicleType == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.VEHICLE_TYPE_MUST_BE_IN_ENUM);
        }
        try {
            VehicleTypeDto.valueOf(Translator.translateToEngOrSelf(rusVehicleType));
        } catch (IllegalArgumentException e) {
            throw new InputFieldValidationException(
                    ValidatorMessages.VEHICLE_TYPE_MUST_BE_IN_ENUM);
        }
    }

    public void validateRusFuelType(String rusFuelType) {
        if (rusFuelType == null) {
            throw new InputFieldValidationException(
                    ValidatorMessages.FUEL_TYPE_MUST_BE_IN_ENUM);
        }
        try {
            FuelTypeDto.valueOf(Translator.translateToEngOrSelf(rusFuelType));
        } catch (IllegalArgumentException e) {
            throw new InputFieldValidationException(
                    ValidatorMessages.FUEL_TYPE_MUST_BE_IN_ENUM);
        }
    }

    public void validateRemoveByIdArgs(String[] inputArgs, String commandSignature) {
        validateCountInputArgs(inputArgs, 1, commandSignature);
        String id = inputArgs[0];
        if (id == null) {
            throw new InputArgsValidationException(
                    ValidatorMessages.PARAMETER_ID_NOT_PASSED);
        }
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InputArgsValidationException(
                    ValidatorMessages.ARGUMENT_ID_MUST_BE_INTEGER);
        }
        if (Long.parseLong(id) < 1) {
            throw new InputArgsValidationException(
                    ValidatorMessages.ARGUMENT_ID_MUST_BE_MORE_ZERO
            );
        }
    }

    public void validateCountLessTypeArgs(String[] inputArgs, String commandSignature) {
        validateCountInputArgs(inputArgs, 1, commandSignature);
        String rusVehicleType = inputArgs[0];
        try {
            validateRusVehicleType(rusVehicleType);
        } catch (InputFieldValidationException e) {
            throw new InputArgsValidationException(e.getMessage());
        }
    }

    public void validateExeScriptArgs(String[] inputArgs, String commandSignature) {
        validateCountInputArgs(inputArgs, 1, commandSignature);
        if (inputArgs[0] == null) {
            throw new InputArgsValidationException(
                    String.format(
                            ValidatorMessages.PARAMETER_FILE_NAME_NOT_PASSED
                    )
            );
        }
    }

    public void validateUpdateByIdArgs(String[] inputArgs, String commandSignature) {
        validateCountInputArgs(inputArgs, 1, commandSignature);
        String id = inputArgs[0];
        if (id == null) {
            throw new InputArgsValidationException(
                    ValidatorMessages.PARAMETER_ID_NOT_PASSED);
        }
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InputArgsValidationException(
                    ValidatorMessages.ARGUMENT_ID_MUST_BE_INTEGER);
        }
        if (Long.parseLong(id) < 1) {
            throw new InputArgsValidationException(
                    ValidatorMessages.ARGUMENT_ID_MUST_BE_MORE_ZERO
            );
        }
    }

    private void validateCountInputArgs(String[] inputArgs, int expectedCount,
                                        String commandSignature) {
        if (inputArgs.length != expectedCount) {
            throw new InputArgsValidationException(
                    String.format(
                            "Количество переданных аргументов не совпадает " +
                                    "с сигнатурой команды: %s",
                            commandSignature
                    )
            );
        }
    }

    public void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InputArgsValidationException(
                    "Имя пользователя не должно быть пустым");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.strip().length() < 8) {
            throw new InputArgsValidationException(
                    "Пароль должен состоять хотя бы из 8 непробельных символов");
        }
    }
}
