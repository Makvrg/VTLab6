package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicle;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.VehicleDtoValidationException;

public class AddIfMinCommand extends VehicleAwareCommand {

    private final CollectionService collectionService;

    public AddIfMinCommand(CollectionService collectionService,
                           ValidatorProvider validatorProvider,
                           StringFormatter formatter) {
        super("add_if_min {element}",
                "добавить новый элемент в коллекцию, если его значение меньше, "
                        + "чем у наименьшего элемента этой коллекции", true,
                validatorProvider, formatter
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request request) {
        if (request instanceof RequestVehicle requestVehicle) {
            VehicleDto vehicleDto = requestVehicle.getVehicleDto();

            try {
                validateVehicleDto(vehicleDto);
            } catch (VehicleDtoValidationException e) {
                return new Response(false, e.getMessage());
            }
            Vehicle vehicle = VehicleMapper.MAPPER.toEntity(vehicleDto);
            if (collectionService.addIfMin(vehicle)) {
                return new Response(true, "Новый объект успешно добавлен в коллекцию");
            }
            return new Response(true, "Новый объект не был добавлен в коллекцию");
        }
        return new Response(false, "Отправлен некорректный запрос");
    }
}
