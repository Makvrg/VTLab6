package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseVehicle;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

import java.util.Optional;

public class MaxByEnginePowerCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public MaxByEnginePowerCommand(CollectionService collectionService,
                                   ValidatorProvider validatorProvider) {
        super("max_by_engine_power",
                "вывести любой объект из коллекции, " +
                        "значение поля enginePower которого является максимальным",
                true
        );
        this.collectionService = collectionService;
        this.validatorProvider = validatorProvider;
    }

    @Override
    public Response execute(Request request) {
        try {
            validatorProvider.getDataValidator().validateUserDto(request.getUserDto());
        } catch (UserDtoValidationException e) {
            return new Response(false, e.getMessage());
        }

        try {
            if (!collectionService.auth(
                    UserMapper.toEntity(
                            request.getUserDto()),
                    request.getUserDto().getPassword())) {
                return new Response(false, "Команды не доступны неавторизованным пользователям");
            }
            Optional<Vehicle> vehicle = collectionService.maxByEnginePower();
            if (vehicle.isPresent()) {
                return new ResponseVehicle(true, "",
                        VehicleMapper.toDto(vehicle.get(),
                                collectionService.findUsernameById(
                                        vehicle.get().getUserId()
                                ).get()));
            } else {
                return new ResponseVehicle(true, "", null);
            }
        } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
            return new Response(false, "ошибка со стороны сервера");
        }
    }
}
