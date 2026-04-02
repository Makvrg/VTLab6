package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.usercontext.UserContext;

public abstract class WithoutParametersCommand extends Command {

    public WithoutParametersCommand(String commandSignature, String commandDescription) {
        super(commandSignature, commandDescription);
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader ignoredReader) {
        validateArgs(inputArgs);
        return new Request(
                commandSignature.split(" ")[0],
                UserContext.getCurrentUser());
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
}
