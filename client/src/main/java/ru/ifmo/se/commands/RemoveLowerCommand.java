package ru.ifmo.se.commands;

import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;

public class RemoveLowerCommand extends RemoveByCompareCommand {

    public RemoveLowerCommand(ValidatorProvider validatorProvider,
                              Printer printer,
                              StringFormatter formatter) {
        super("remove_lower {element}",
                "удалить из коллекции все элементы, меньшие, чем заданный",
                validatorProvider, printer, formatter);
    }
}
