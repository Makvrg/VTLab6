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
import ru.ifmo.se.usercontext.UserContext;
import ru.ifmo.se.usercontext.UserType;

@RequiredArgsConstructor
public class Pipeline implements Runnable, ShutdownListener {

    private final Printer printer;
    private final InputManager inputManager;
    private final CommandInvoker commandInvoker;
    private final NetworkService networkService;
    private boolean shutdown = false;

    public void run() {
        printer.forcePrintln("Приложение запускается");
        while (!shutdown) {
            String[] input;
            try {
                input = inputManager.readInput();
            } catch (EndOfFileException | IORuntimeException e) {
                continue;
            }
            String commandName = InputTextHandler.parseCommandName(input);
            String[] inputArgs = InputTextHandler.parseInputArgs(input);

            Request request;
            try {
                if (UserContext.getCurrentUserType() == UserType.GUEST &&
                        !commandName.equals(commandInvoker.getRegCommandName()) &&
                        !commandName.equals(commandInvoker.getAuthCommandName()) &&
                        !commandName.equals(commandInvoker.getExitCommandName()) &&
                        !commandName.equals(commandInvoker.getHelpCommandName())) {
                    printer.forcePrintln(
                            "Для начала работы необходимо пройти аутентификацию"
                    );
                    continue;
                }
                request = commandInvoker.invokeMakeRequest(commandName, inputArgs);
            } catch (InputArgsValidationException e) {
                printer.forcePrintln(e.getMessage() + "\nПовторите ввод");
                continue;
            }
            if (request == null) {
                continue;
            }

            Response response;
            try {
                response = networkService.send(request);
            } catch (NetworkException e) {
                printer.forcePrintln(e.getMessage());
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
