package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.print.Printer;

import java.util.ArrayList;
import java.util.List;

public class ExitCommand extends Command {

    private final Printer printer;
    private final List<ShutdownListener> listeners = new ArrayList<>();

    public ExitCommand(Printer printer) {
        super("exit", "завершить программу");
        this.printer = printer;
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader reader) {
        validateArgs(inputArgs);
        listeners.forEach(ShutdownListener::onShutdown);
        printer.forcePrintln("Закрытие приложения");
        return null;
    }

    @Override
    public void handleResponse(Response response) {
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
        if (inputArgs.length != 0) {
            throw new InputArgsValidationException(
                    String.format(
                            "Количество переданных аргументов не совпадает " +
                                    "с сигнатурой команды: %s",
                            getCommandSignature()
                    )
            );
        }
    }

    public void addShutdownListeners(List<ShutdownListener> listeners) {
        this.listeners.addAll(listeners);
    }
}
