package ru.ifmo.se.commands;

import ru.ifmo.se.entity.User;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;

public class RemoveLowerCommand extends RemoveByCompareCommand {

    private final CollectionService collectionService;

    public RemoveLowerCommand(CollectionService collectionService,
                              ValidatorProvider validatorProvider,
                              StringFormatter formatter) {
        super("remove_lower {element}",
                "удалить из коллекции все элементы, меньшие, чем заданный",
                true,
                validatorProvider, formatter
        );
        this.collectionService = collectionService;
    }

    @Override
    protected boolean useService(Vehicle vehicle, String username) {
        return collectionService.removeLower(vehicle, username);
    }

    @Override
    protected boolean checkAuth(User enteredUser, String rawPassword) {
        return collectionService.auth(enteredUser, rawPassword);
    }
}
