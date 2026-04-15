package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicleId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;
import ru.ifmo.se.validator.exceptions.VehicleDtoValidationException;

public class UpdateByIdCommand extends VehicleAwareCommand {

    private final CollectionService collectionService;

    public UpdateByIdCommand(CollectionService collectionService,
                             ValidatorProvider validatorProvider,
                             StringFormatter formatter) {
        super("update id {element}",
                "обновить значение элемента коллекции, id которого равен заданному",
                true,
                validatorProvider, formatter
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request request) {
        if (request instanceof RequestVehicleId requestVehicleId) {
            try {
                validatorProvider.getDataValidator().validateUserDto(request.getUserDto());
            } catch (UserDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            VehicleDto vehicleDto = requestVehicleId.getVehicleDto();
            Long id = requestVehicleId.getId();
            try {
                validateVehicleDto(vehicleDto);
            } catch (VehicleDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Vehicle vehicle = VehicleMapper.toEntity(vehicleDto, null);

            try {
                if (!collectionService.auth(
                        UserMapper.toEntity(
                                request.getUserDto()),
                        request.getUserDto().getPassword())) {
                    return new Response(false, "Команды не доступны неавторизованным пользователям");
                }
                if (collectionService.updateById(vehicle, id, request.getUserDto().getUsername())) {
                    return new Response(true, "Объект успешно обновлён");
                }
                return new Response(true,
                        "Объект не был обновлён, так как в коллекции нет объекта с данным id");
            } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
                return new Response(false, "ошибка со стороны сервера");
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
