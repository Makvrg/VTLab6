package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestType;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCount;
import ru.ifmo.se.entity.VehicleType;
import ru.ifmo.se.mapper.UserMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.NoSuchAlgorithmRuntimeException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.CountLessThanTypeValidationException;
import ru.ifmo.se.validator.exceptions.UserDtoValidationException;

public class CountLessThanTypeCommand extends Command {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;

    public CountLessThanTypeCommand(CollectionService collectionService,
                                    ValidatorProvider validatorProvider) {
        super("count_less_than_type type",
                "вывести количество элементов, значение поля type которых меньше заданного",
                true
        );
        this.collectionService = collectionService;
        this.validatorProvider = validatorProvider;
    }

    @Override
    public Response execute(Request request) {
        if (request instanceof RequestType requestType) {
            try {
                validatorProvider.getDataValidator().validateUserDto(request.getUserDto());
            } catch (UserDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            try {
                validatorProvider.getDataValidator()
                        .validateCountLessType(requestType.getVehicleTypeDto());
            } catch (CountLessThanTypeValidationException e) {
                return new Response(false, e.getMessage());
            }
            VehicleType vehicleType = VehicleType.valueOf(
                    requestType.getVehicleTypeDto().name());

            try {
                if (!collectionService.auth(
                        UserMapper.toEntity(
                                request.getUserDto()),
                        request.getUserDto().getPassword())) {
                    return new Response(false, "Команды не доступны неавторизованным пользователям");
                }
                if (collectionService.getCountElementsCollection() != 0) {
                    long countVeh = collectionService.countLessThanType(vehicleType);
                    return new ResponseCount(true,
                            "", countVeh);
                } else {
                    return new ResponseCount(true, "", 0L);
                }
            } catch (SQLRuntimeException | NoSuchAlgorithmRuntimeException e) {
                return new Response(false, "ошибка со стороны сервера");
            }
        } else {
            return new Response(false, "Отправлен некорректный запрос");
        }
    }
}
