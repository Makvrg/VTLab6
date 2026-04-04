package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicle;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.RemoveByIdIllegalStateException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;
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
            try {
                validatorProvider.getDataValidator().validateUserDto(request.getUserDto());
            } catch (UserDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            VehicleDto vehicleDto = requestVehicle.getVehicleDto();
            try {
                validateVehicleDto(vehicleDto);
            } catch (VehicleDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Vehicle vehicle = VehicleMapper.toEntity(vehicleDto, null);
            try {
                if (!checkAuth(UserMapper.toEntity(
                        request.getUserDto()),
                        request.getUserDto().getPassword())) {
                    return new Response(false, "Команды не доступны неавторизованным пользователям");
                }
                if (useService(vehicle)) {
                    return new Response(true, "Прошло успешное удаление");
                }
                return new Response(true, "Объекты для удаления не были найдены");
            } catch (RemoveByIdIllegalStateException e) {
                return new Response(false,
                        "Объекты не были удалены, так как произошла ошибка во время работы: " +
                                e.getMessage()
                );
            } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
                return new Response(false, "ошибка со стороны сервера");
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }

    protected abstract boolean useService(Vehicle vehicle);

    protected abstract boolean checkAuth(User enteredUser, String rawPassword);
}
