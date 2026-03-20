package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseNumberOfGroups;
import ru.ifmo.se.service.CollectionService;

import java.util.HashMap;
import java.util.Map;

public class GroupCountingByDistanceTravelledCommand extends Command {

    private final CollectionService collectionService;


    public GroupCountingByDistanceTravelledCommand(CollectionService collectionService) {
        super("group_counting_by_distance_travelled",
                "сгруппировать элементы коллекции по значению поля distanceTravelled, " +
                        "вывести количество элементов в каждой группе", true
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        Map<Float, Integer> numberOfGroups =
                collectionService.groupCountingByDistanceTravelled();
        if (!numberOfGroups.isEmpty()) {
            return new ResponseNumberOfGroups(true, "", numberOfGroups);
        } else {
            return new ResponseNumberOfGroups(true, "", new HashMap<>());
        }
    }
}
