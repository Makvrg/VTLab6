package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestType;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCount;
import ru.ifmo.se.entity.VehicleType;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.CountLessThanTypeValidationException;

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
                validatorProvider.getDataValidator()
                        .validateCountLessType(requestType.getVehicleTypeDto());
            } catch (CountLessThanTypeValidationException e) {
                return new Response(false, e.getMessage());
            }
            VehicleType vehicleType = VehicleType.valueOf(
                    requestType.getVehicleTypeDto().name());
            if (collectionService.getCountElementsCollection() != 0) {
                long countVeh = collectionService.countLessThanType(vehicleType);
                return new ResponseCount(true,
                        "", countVeh);
            } else {
                return new ResponseCount(true, "", 0L);
            }
        } else {
            return new Response(false, "Отправлен некорректный запрос");
        }
    }
}
