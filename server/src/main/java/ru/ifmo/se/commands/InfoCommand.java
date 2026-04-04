package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCollectionInfo;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

public class InfoCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public InfoCommand(CollectionService collectionService,
                       ValidatorProvider validatorProvider) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции "
                + "(тип, дата инициализации, тип и количество элементов)", true
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
            return new ResponseCollectionInfo(true, "", collectionService.info());
        } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
            return new Response(false, "ошибка со стороны сервера");
        }
    }
}
