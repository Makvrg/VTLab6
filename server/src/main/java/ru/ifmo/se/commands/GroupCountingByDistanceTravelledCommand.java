package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseNumberOfGroups;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

import java.util.HashMap;
import java.util.Map;

public class GroupCountingByDistanceTravelledCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public GroupCountingByDistanceTravelledCommand(CollectionService collectionService,
                                                   ValidatorProvider validatorProvider) {
        super("group_counting_by_distance_travelled",
                "сгруппировать элементы коллекции по значению поля distanceTravelled, " +
                        "вывести количество элементов в каждой группе", true
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
            Map<Float, Integer> numberOfGroups =
                    collectionService.groupCountingByDistanceTravelled();
            if (!numberOfGroups.isEmpty()) {
                return new ResponseNumberOfGroups(true, "", numberOfGroups);
            } else {
                return new ResponseNumberOfGroups(true, "", new HashMap<>());
            }
        } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
            return new Response(false, "ошибка со стороны сервера");
        }
    }
}
