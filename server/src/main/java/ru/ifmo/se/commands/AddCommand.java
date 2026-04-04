package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicle;
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

public class AddCommand extends VehicleAwareCommand {

    private final CollectionService collectionService;

    public AddCommand(CollectionService collectionService,
                      ValidatorProvider validatorProvider,
                      StringFormatter formatter) {
        super("add {element}", "добавить новый элемент в коллекцию", true,
                validatorProvider, formatter
        );
        this.collectionService = collectionService;
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
                if (!collectionService.auth(
                        UserMapper.toEntity(
                                request.getUserDto()),
                                request.getUserDto().getPassword())) {
                    return new Response(false, "Команды не доступны неавторизованным пользователям");
                }
                if (collectionService.add(vehicle, request.getUserDto().getUsername())) {
                    return new Response(true, "Новый объект успешно добавлен в коллекцию");
                }
                return new Response(false, "Новый объект не был добавлен в коллекцию");
            } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
                return new Response(false, "ошибка со стороны сервера");
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
