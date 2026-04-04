package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.RemoveByIdIllegalStateException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.RemoveByIdValidationException;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

public class RemoveByIdCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public RemoveByIdCommand(CollectionService collectionService,
                             ValidatorProvider validatorProvider) {
        super("remove_by_id id", "удалить элемент из коллекции по его id", true);
        this.collectionService = collectionService;
        this.validatorProvider = validatorProvider;
    }

    @Override
    public Response execute(Request request) {
        if (request instanceof RequestId requestId) {
            try {
                validatorProvider.getDataValidator().validateUserDto(request.getUserDto());
            } catch (UserDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Long id = requestId.getId();

            try {
                validatorProvider.getDataValidator().validateRemoveById(id);
                if (!collectionService.auth(
                        UserMapper.toEntity(
                                request.getUserDto()),
                        request.getUserDto().getPassword())) {
                    return new Response(false, "Команды не доступны неавторизованным пользователям");
                }
                if (collectionService.removeVehicleById(id)) {
                    return new Response(true,
                            "Объект Vehicle успешно удалён из коллекции по заданному id");
                }
                return new Response(true,
                        "Объект Vehicle с заданным id не найден в коллекции");
            } catch (RemoveByIdValidationException e) {
                return new Response(false, e.getMessage());
            } catch (RemoveByIdIllegalStateException e) {
                return new Response(false,
                        "Объект не удалён, так как произошла ошибка во время работы: " +
                                e.getMessage()
                );
            } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
                return new Response(false, "ошибка со стороны сервера");
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
