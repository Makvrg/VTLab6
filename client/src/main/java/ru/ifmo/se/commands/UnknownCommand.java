package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.print.Printer;

public class UnknownCommand extends Command {

    private final Printer printer;

    public UnknownCommand(Printer printer) {
        super("unknown",
                "вызывается автоматически при вводе команды, "
                        + "которая не поддерживается программой"
        );
        this.printer = printer;
    }

    @Override
    public Request makeRequest(String[] inputCommand, Reader ignoredReader) {
        printer.forcePrintln(
                String.format(
                        "Передана неизвестная команда: \"%s\"", inputCommand[0]
                )
        );
        return null;
    }

    @Override
    public void handleResponse(Response response) {
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
    }
}
