package ru.ifmo.se.io.input;

import lombok.Getter;
import ru.ifmo.se.commands.*;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.event.ShutdownListener;
import ru.ifmo.se.io.input.readers.factory.ReaderFactory;
import ru.ifmo.se.io.input.readers.file.DataProvider;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.ExecuteScriptException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommandInvoker {

    private final DataProvider dataProvider;
    private final ReaderFactory readerFactory;
    private final InputManager inputManager;
    private final StringFormatter formatter;
    private final Map<String, Command> commands;
    private String unknownCommandName;
    @Getter
    private String exitCommandName;
    @Getter
    private String helpCommandName;
    @Getter
    private String regCommandName;
    @Getter
    private String authCommandName;

    public CommandInvoker(DataProvider dataProvider,
                          ReaderFactory readerFactory,
                          ValidatorProvider validatorProvider,
                          StringFormatter formatter,
                          Printer printer,
                          InputManager inputManager) {
        this.dataProvider = dataProvider;
        this.readerFactory = readerFactory;
        this.inputManager = inputManager;
        this.formatter = formatter;
        commands = buildMapOfCommands(validatorProvider, printer);
    }

    public Request invokeMakeRequest(String commandName, String[] inputArgs) {
        if (commands.containsKey(commandName)) {
            try {
                return commands.get(commandName)
                        .makeRequest(inputArgs,
                                inputManager.getReaders().get(
                                        inputManager.getReaders().size() - 1
                                )
                        );
            } catch (ExecuteScriptException e) {
                inputManager.removeCurrentReader();
                return null;
            }
        } else {
            return commands.get(unknownCommandName)
                    .makeRequest(new String[]{commandName}, null);
        }
    }

    public void invokeHandleResponse(String commandName, Response response) {
        if (!commandName.equals(unknownCommandName) && commands.containsKey(commandName)) {
            commands.get(commandName).handleResponse(response);
        }
    }

    public void addListenersToExitCommand(List<ShutdownListener> listeners) {
        ((ExitCommand) commands.get(exitCommandName)).addShutdownListeners(listeners);
    }

    private Map<String, Command> buildMapOfCommands(
            ValidatorProvider validatorProvider,
            Printer printer) {
        Map<String, Command> commands = new LinkedHashMap<>();
        Command currentCommand;
        Function<Command, String> getCommandName = 
                cmd -> cmd.getCommandSignature().split(" ")[0];
        
        currentCommand = new UnknownCommand(printer);
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        unknownCommandName = getCommandName.apply(currentCommand);
        
        currentCommand = new HelpCommand(printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        helpCommandName = getCommandName.apply(currentCommand);

        currentCommand = new RegistrationCommand(printer, validatorProvider);
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        regCommandName = getCommandName.apply(currentCommand);

        currentCommand = new AuthCommand(printer, validatorProvider);
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        authCommandName = getCommandName.apply(currentCommand);

        currentCommand = new InfoCommand(printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ShowCommand(printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new AddCommand(validatorProvider, printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new UpdateByIdCommand(validatorProvider, printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveByIdCommand(validatorProvider, printer);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ClearCommand(printer);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ExecuteScriptCommand(
                dataProvider, readerFactory,
                inputManager.getReaders(), printer, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ExitCommand(printer);
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        exitCommandName = getCommandName.apply(currentCommand);

        currentCommand = new AddIfMinCommand(validatorProvider, printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveGreaterCommand(validatorProvider, printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveLowerCommand(validatorProvider, printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new MaxByEnginePowerCommand(printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new GroupCountingByDistanceTravelledCommand(printer, formatter);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new CountLessThanTypeCommand(validatorProvider, printer);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        return commands;
    }
}
