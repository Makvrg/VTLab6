package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.output.print.Printer;

public class ClearCommand extends WithoutParametersCommand {

    private final Printer printer;

    public ClearCommand(Printer printer) {
        super("clear", "очистить коллекцию");
        this.printer = printer;
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Сервер не смог выполнить команду");
            return;
        }
        printer.forcePrintln("Коллекция успешно очищена");
    }
}
