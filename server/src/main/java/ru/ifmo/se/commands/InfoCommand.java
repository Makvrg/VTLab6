package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCollectionInfo;
import ru.ifmo.se.service.CollectionService;

public class InfoCommand extends Command {

    private final CollectionService collectionService;

    public InfoCommand(CollectionService collectionService) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции "
                + "(тип, дата инициализации, тип и количество элементов)", true
        );
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        return new ResponseCollectionInfo(true, "",collectionService.info());
    }
}
