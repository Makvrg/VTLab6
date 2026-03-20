package ru.ifmo.se.commands;

import jakarta.validation.ConstraintViolation;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.entity.fieldnames.VehicleFieldNames;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.VehicleDtoValidationException;

import java.util.Set;

public abstract class VehicleAwareCommand extends Command {

    private final ValidatorProvider validatorProvider;
    private final StringFormatter formatter;

    protected VehicleAwareCommand(
            String commandSignature,
            String commandDescription,
            boolean clientAccess,
            ValidatorProvider validatorProvider,
            StringFormatter formatter) {
        super(commandSignature, commandDescription, clientAccess);
        this.validatorProvider = validatorProvider;
        this.formatter = formatter;
    }

    protected void validateVehicleDto(VehicleDto vehicleDto) {
        for (Vehicle.FieldNames field : Vehicle.FieldNames.values()) {
            if (field == Vehicle.FieldNames.ID ||
                    field == Vehicle.FieldNames.CREATION_DATE) {
               continue;
            }
            String fieldPath;
            if (field == Vehicle.FieldNames.X || field == Vehicle.FieldNames.Y) {
                fieldPath = "coordinates." + field.getTitle();
            } else {
                fieldPath = field.getTitle();
            }
            Set<? extends ConstraintViolation<?>> validateViols =
                    validatorProvider.getBeanValidator()
                            .validateProperty(vehicleDto, fieldPath);
            if (!validateViols.isEmpty()) {
                throw new VehicleDtoValidationException(
                        formatter.formatFieldViolations(validateViols));
            }
        }
        validatorProvider.getDataValidator().validateVehicleDto(vehicleDto);
    }
}
