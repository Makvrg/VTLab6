package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseVehicle;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;

import java.util.Optional;

public class MaxByEnginePowerCommand extends Command {

    private final CollectionService collectionService;

    public MaxByEnginePowerCommand(CollectionService collectionService) {
        super("max_by_engine_power",
                "вывести любой объект из коллекции, " +
                        "значение поля enginePower которого является максимальным",
                true
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        Optional<Vehicle> vehicle = collectionService.maxByEnginePower();
        if (vehicle.isPresent()) {
            return new ResponseVehicle(true, "",
                    VehicleMapper.MAPPER.toDto(vehicle.get()));
        } else {
            return new ResponseVehicle(true, "", null);
        }
    }
}
