package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicle;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.exceptions.RemoveByIdIllegalStateException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.VehicleDtoValidationException;

public abstract class RemoveByCompareCommand extends VehicleAwareCommand {

    protected RemoveByCompareCommand(
            String commandSignature,
            String commandDescription,
            boolean clientAccess,
            ValidatorProvider validatorProvider,
            StringFormatter formatter) {
        super(commandSignature, commandDescription, clientAccess,
                validatorProvider, formatter);
    }

    @Override
    public Response execute(Request request) {
        if (request instanceof RequestVehicle requestVehicle) {
            VehicleDto vehicleDto = requestVehicle.getVehicleDto();

            try {
                validateVehicleDto(vehicleDto);
            } catch (VehicleDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Vehicle vehicle = VehicleMapper.MAPPER.toEntity(vehicleDto);
            try {
                if (useService(vehicle)) {
                    return new Response(true, "Прошло успешное удаление");
                }
                return new Response(true, "Объекты для удаления не были найдены");
            } catch (RemoveByIdIllegalStateException e) {
                return new Response(false,
                        "Объекты не были удалены, так как произошла ошибка во время работы: " +
                                e.getMessage()
                );
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }

    protected abstract boolean useService(Vehicle vehicle);
}
