package ru.ifmo.se.application;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.TerminalInputManager;
import ru.ifmo.se.io.input.exceptions.IORuntimeException;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.InputTextHandler;
import ru.ifmo.se.io.output.print.Printer;

@RequiredArgsConstructor
public class TerminalPipeline implements Runnable, ShutdownListener {

    private final Printer printer;
    private final TerminalInputManager terminalInputManager;
    private final CommandInvoker commandInvoker;
    private volatile boolean shutdown = false;

    private String[] input;
    private String commandName;
    private String[] inputArgs;

    public void run() {
        printer.forcePrintln("Приложение запускается");
        while (!shutdown) {
            try {
                input = terminalInputManager.readInput();
            } catch (IORuntimeException e) {
                continue;
            }
            commandName = InputTextHandler.parseCommandName(input);
            inputArgs = InputTextHandler.parseInputArgs(input);

            try {
                commandInvoker.invokeLocalCommand(commandName);
            } catch (InputArgsValidationException e) {
                printer.forcePrintln(e.getMessage() + "\nПовторите ввод");
            }
        }
    }

    @Override
    public void onShutdown() {
        shutdown = true;
    }
}
