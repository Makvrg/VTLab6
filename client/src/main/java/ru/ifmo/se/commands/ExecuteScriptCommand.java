package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.factory.ReaderCreateException;
import ru.ifmo.se.io.input.readers.factory.ReaderFactory;
import ru.ifmo.se.io.input.readers.file.DataProvider;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;

import java.util.List;

public class ExecuteScriptCommand extends Command {

    private final DataProvider dataProvider;
    private final ReaderFactory readerFactory;
    private final List<Reader> inputManagerReaders;
    private final Printer printer;
    private final ValidatorProvider validatorProvider;

    public ExecuteScriptCommand(DataProvider dataProvider,
                                ReaderFactory readerFactory,
                                List<Reader> inputManagerReaders,
                                Printer printer,
                                ValidatorProvider validatorProvider) {
        super("execute_script file_name",
                "считать и исполнить скрипт из указанного файла. В скрипте содержатся "
                        + "команды в таком же виде, в котором их вводит пользователь "
                        + "в интерактивном режиме."
        );
        this.dataProvider = dataProvider;
        this.readerFactory = readerFactory;
        this.inputManagerReaders = inputManagerReaders;
        this.printer = printer;
        this.validatorProvider = validatorProvider;
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
        validatorProvider.getDataValidator().validateExeScriptArgs(
                inputArgs, getCommandSignature());
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader ignoredReader) {
        validateArgs(inputArgs);

        String fileName = inputArgs[0];
        Reader currentFileReader;
        try {
            currentFileReader = readerFactory.createFileReader(
                    fileName,
                    dataProvider
            );
        } catch (ReaderCreateException e) {
            printer.forcePrintln(
                    "Файл с указанным названием не найден или к нему нет доступа");
            return null;
        }

        for (Reader reader : inputManagerReaders) {
            if (reader.getName().equals(currentFileReader.getName())) {
                printer.forcePrintln(
                        "Во избежание рекурсии выполняется "
                                + "принудительное завершение всей цепочки скриптов");
                while (inputManagerReaders.size() > 1) {
                    inputManagerReaders.remove(inputManagerReaders.size() - 1);
                }
                printer.forcePrintln("Активен режим чтения терминала");
                printer.on();
                return null;
            }
        }

        inputManagerReaders.add(currentFileReader);
        printer.forcePrintln("Активен режим чтения файла " + currentFileReader.getName());
        printer.off();
        return null;
    }

    @Override
    public void handleResponse(Response response) {
    }
}
