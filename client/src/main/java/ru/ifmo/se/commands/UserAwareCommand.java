package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.UserDto;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.file.FileReader;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.ExecuteScriptException;

import java.io.IOException;

public abstract class UserAwareCommand extends Command {

    protected UserDto user;

    protected final Printer printer;
    private final ValidatorProvider validatorProvider;

    public UserAwareCommand(String commandSignature, String commandDescription,
                            Printer printer, ValidatorProvider validatorProvider) {
        super(commandSignature, commandDescription);
        this.printer = printer;
        this.validatorProvider = validatorProvider;
    }

    protected UserDto readUser(Reader reader) {
        String username = null;
        String password = null;
        boolean valid = false;
        while (!valid) {
            try {
                username = readField(reader, "Придумайте имя пользователя");
                printer.printlnIfOn("");
                validatorProvider.getDataValidator().validateUsername(username);
                valid = true;
            } catch (InputArgsValidationException e) {
                printer.printlnIfOn("\n" + e.getMessage() + ", повторите ввод");
            }
        }
        valid = false;
        while (!valid) {
            try {
                password = readField(reader, "Придумайте пароль");
                printer.printlnIfOn("");
                validatorProvider.getDataValidator().validatePassword(password);
                valid = true;
            } catch (InputArgsValidationException e) {
                printer.printlnIfOn(e.getMessage() + ", повторите ввод");
            }
        }
        return new UserDto(username, password);
    }

    protected String readField(Reader reader, String message) {
        printer.printIfOn(message + " > ");
        try {
            String inputString = reader.readLine();
            if (inputString == null) {
                if (reader instanceof FileReader) {
                    throw new ExecuteScriptException(
                            "Неожиданное количество строк данных в файле"
                    );
                }
                inputString = "";
                printer.forcePrintln("");
            }
            return inputString.strip();
        } catch (IOException e) {
            throw new ExecuteScriptException(
                    "Файл с указанным названием не найден или к нему нет доступа"
            );
        }
    }
}
