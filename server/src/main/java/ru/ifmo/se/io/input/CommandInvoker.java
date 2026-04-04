package ru.ifmo.se.io.input;

import ru.ifmo.se.commands.*;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandInvoker {

    private final Map<String, Command> commands;
    private String unknownCommandName;

    public CommandInvoker(ValidatorProvider validatorProvider,
                          CollectionService collectionService,
                          StringFormatter formatter,
                          Printer printer) {
        commands = buildMapOfCommands(
                collectionService,
                validatorProvider,
                formatter,
                printer
        );
    }

    public Response invokeCommand(Request request) {
        if (commands.containsKey(request.getCommandName()) &&
                commands.get(request.getCommandName()).isClientAccess()) {
            return commands.get(request.getCommandName())
                    .execute(request);
        }
        return null;
    }

    public void invokeLocalCommand(String commandName) {
        if (commands.containsKey(commandName) &&
                !commands.get(commandName).isClientAccess()) {
            commands.get(commandName)
                    .execute(null);
        }
    }

    private Map<String, Command> buildMapOfCommands(
            CollectionService collectionService,
            ValidatorProvider validatorProvider,
            StringFormatter formatter,
            Printer printer) {
        Map<String, Command> commands = new LinkedHashMap<>();
        Command currentCommand;
        Function<Command, String> getCommandName = 
                cmd -> cmd.getCommandSignature().split(" ")[0];
        
        currentCommand = new UnknownCommand();
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        unknownCommandName = getCommandName.apply(currentCommand);

        currentCommand = new RegistrationCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new AuthCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        
        currentCommand = new HelpCommand(commands.values());
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new InfoCommand(collectionService, validatorProvider);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ExitCommand(printer, collectionService);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ShowCommand(collectionService, validatorProvider);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new AddCommand(
                collectionService, validatorProvider, formatter
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new UpdateByIdCommand(
                collectionService, validatorProvider, formatter
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveByIdCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ClearCommand(collectionService, validatorProvider);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new AddIfMinCommand(
                collectionService, validatorProvider, formatter
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveGreaterCommand(
                collectionService, validatorProvider, formatter
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new RemoveLowerCommand(
                collectionService, validatorProvider, formatter
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new MaxByEnginePowerCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new GroupCountingByDistanceTravelledCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new CountLessThanTypeCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        return commands;
    }
}
