package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.UserDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.usercontext.UserContext;
import ru.ifmo.se.validator.ValidatorProvider;

public class RegistrationCommand extends UserAwareCommand {

    public RegistrationCommand(Printer printer, ValidatorProvider validatorProvider) {
        super("registration", "зарегистрировать нового пользователя и войти",
                printer, validatorProvider);
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader reader) {
        validateArgs(inputArgs);
        UserDto user = readUser(reader);
        this.user = user;
        return new Request("registration", user);
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

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Не удалось зарегистрировать пользователя: " +
                    response.getMessage());
            return;
        }
        printer.forcePrintln("Пользователь успешно зарегистрирован");
        UserContext.setCurrentUser(user);
    }
}
