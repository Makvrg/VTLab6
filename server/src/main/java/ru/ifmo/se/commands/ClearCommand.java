package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.service.CollectionService;

public class ClearCommand extends Command {

    private final CollectionService collectionService;

    public ClearCommand(CollectionService collectionService) {
        super("clear", "очистить коллекцию", true);
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        if (collectionService.clear()) {
            return new Response(true, "");
        } else {
            return new Response(false, "");
        }
    }
}
