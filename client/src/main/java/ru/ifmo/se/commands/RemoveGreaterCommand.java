package ru.ifmo.se.commands;

import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;

public class RemoveGreaterCommand extends RemoveByCompareCommand {

    public RemoveGreaterCommand(ValidatorProvider validatorProvider,
                                Printer printer,
                                StringFormatter formatter) {
        super("remove_greater {element}",
                "удалить из коллекции все элементы, превышающие заданный",
                validatorProvider, printer, formatter);
    }
}
