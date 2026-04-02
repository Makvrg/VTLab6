package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseListVehicle;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.mapper.VehicleMapper;
import ru.ifmo.se.service.CollectionService;

import java.util.ArrayList;
import java.util.Collection;

public class ShowCommand extends Command {

    private final CollectionService collectionService;

    public ShowCommand(CollectionService collectionService) {
        super("show",
                "вывести в стандартный поток вывода все элементы "
                        + "коллекции в строковом представлении", true
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        Collection<Vehicle> vehicles = collectionService.showVehicles();
        if (!vehicles.isEmpty()) {
            return new ResponseListVehicle(true, "",
                    vehicles.stream().map(VehicleMapper.MAPPER::toDto).toList());
        } else {
            return new ResponseListVehicle(true, "Коллекция пуста", new ArrayList<>());
        }
    }
}
