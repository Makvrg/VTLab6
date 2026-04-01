package ru.ifmo.se.io.input;

import lombok.Getter;
import ru.ifmo.se.commands.*;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.env.EnvVariableProvider;
import ru.ifmo.se.io.output.filewriter.FileWriter;
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
    @Getter
    private String saveCommandName;

    public CommandInvoker(ValidatorProvider validatorProvider,
                          CollectionService collectionService,
                          StringFormatter formatter,
                          FileWriter<Vehicle> fileWriter,
                          EnvVariableProvider envProvider,
                          Printer printer) {
        commands = buildMapOfCommands(
                collectionService,
                validatorProvider,
                fileWriter,
                formatter,
                envProvider,
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
            FileWriter<Vehicle> fileWriter,
            StringFormatter formatter,
            EnvVariableProvider envProvider,
            Printer printer) {
        Map<String, Command> commands = new LinkedHashMap<>();
        Command currentCommand;
        Function<Command, String> getCommandName = 
                cmd -> cmd.getCommandSignature().split(" ")[0];
        
        currentCommand = new UnknownCommand();
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        unknownCommandName = getCommandName.apply(currentCommand);
        
        currentCommand = new HelpCommand(commands.values());
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new InfoCommand(collectionService);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ExitCommand(printer, collectionService);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new ShowCommand(collectionService);
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

        currentCommand = new ClearCommand(collectionService);
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new SaveCommand(
                collectionService, fileWriter, envProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);
        saveCommandName = getCommandName.apply(currentCommand);

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
                collectionService
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new GroupCountingByDistanceTravelledCommand(
                collectionService
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        currentCommand = new CountLessThanTypeCommand(
                collectionService, validatorProvider
        );
        commands.put(getCommandName.apply(currentCommand), currentCommand);

        return commands;
    }
}
