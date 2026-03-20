package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicleId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;
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
            VehicleDto vehicleDto = requestVehicleId.getVehicleDto();
            Long id = requestVehicleId.getId();

            try {
                validateVehicleDto(vehicleDto);
            } catch (VehicleDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Vehicle vehicle = VehicleMapper.MAPPER.toEntity(vehicleDto);
            if (collectionService.updateById(vehicle, id)) {
                return new Response(true, "Объект успешно обновлён");
            }
            return new Response(true,
                    "Объект не был обновлён, так как в коллекции нет объекта с данным id");
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
