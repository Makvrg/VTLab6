package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.RemoveByIdIllegalStateException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.RemoveByIdValidationException;

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
            Long id = requestId.getId();

            try {
                validatorProvider.getDataValidator().validateRemoveById(id);
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
            }
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
