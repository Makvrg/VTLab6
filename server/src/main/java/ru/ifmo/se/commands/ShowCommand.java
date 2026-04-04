package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseListVehicle;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class ShowCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public ShowCommand(CollectionService collectionService,
                       ValidatorProvider validatorProvider) {
        super("show",
                "вывести в стандартный поток вывода все элементы "
                        + "коллекции в строковом представлении", true
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
            Collection<Vehicle> vehicles = collectionService.showVehicles();
            if (!vehicles.isEmpty()) {
                return new ResponseListVehicle(true, "",
                        vehicles.stream().map(
                                veh -> VehicleMapper.toDto(
                                        veh,
                                        collectionService.findUsernameById(veh.getUserId()).get()
                                )
                        ).toList()
                );
            } else {
                return new ResponseListVehicle(true, "Коллекция пуста", new ArrayList<>());
            }
        } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
            return new Response(false, "ошибка со стороны сервера");
        }
    }
}
