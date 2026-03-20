package ru.ifmo.se.application;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.InputManager;
import ru.ifmo.se.io.input.exceptions.EndOfFileException;
import ru.ifmo.se.io.input.exceptions.IORuntimeException;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.InputTextHandler;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.network.NetworkException;
import ru.ifmo.se.network.NetworkService;

@RequiredArgsConstructor
public class Pipeline implements Runnable, ShutdownListener {

    private final Printer printer;
    private final InputManager inputManager;
    private final CommandInvoker commandInvoker;
    private final NetworkService networkService;
    private boolean shutdown = false;

    private String[] input;
    private String commandName;
    private String[] inputArgs;
    private Request request;
    private Response response;

    public void run() {
        printer.forcePrintln("Приложение запускается");
        while (!shutdown) {
            try {
                input = inputManager.readInput();
            } catch (EndOfFileException | IORuntimeException e) {
                continue;
            }
            commandName = InputTextHandler.parseCommandName(input);
            inputArgs = InputTextHandler.parseInputArgs(input);

            try {
                request = commandInvoker.invokeMakeRequest(commandName, inputArgs);
            } catch (InputArgsValidationException e) {
                printer.forcePrintln(e.getMessage() + "\nПовторите ввод");
                continue;
            }
            if (request == null) {  // Выполнилась команда, выполняемая локально
                continue;
            }

            try {
                response = networkService.send(request);
            } catch (NetworkException e) {
                printer.forcePrintln(e.getMessage());
                // Выключение клиента командой Exit
                /*commandInvoker.invokeMakeRequest(
                        commandInvoker.getExitCommandName(), inputArgs);*/
                continue;
            }
            commandInvoker.invokeHandleResponse(commandName, response);
        }
    }

    @Override
    public void onShutdown() {
        shutdown = true;
    }
}
