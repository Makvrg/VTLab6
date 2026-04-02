package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.VehicleTypeDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestType;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCount;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.io.output.translator.Translator;
import ru.ifmo.se.usercontext.UserContext;
import ru.ifmo.se.validator.ValidatorProvider;

public class CountLessThanTypeCommand extends Command {

    private final ValidatorProvider validatorProvider;
    private final Printer printer;

    public CountLessThanTypeCommand(ValidatorProvider validatorProvider,
                                    Printer printer) {
        super("count_less_than_type type",
                "вывести количество элементов, значение поля type которых меньше заданного");
        this.validatorProvider = validatorProvider;
        this.printer = printer;
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader ignoredReader) {
        validateArgs(inputArgs);
        return new RequestType(
                commandSignature.split(" ")[0],
                VehicleTypeDto.valueOf(Translator.translateToEngOrSelf(inputArgs[0])),
                UserContext.getCurrentUser()
        );
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Ошибка валидации");
            printer.forcePrintln(response.getMessage());
            return;
        }
        if (response instanceof ResponseCount responseCount) {
            printer.forcePrintln("Количество элементов равно " +
                    responseCount.getCount());
        } else {
            printer.forcePrintln(
                    "Не получилось получить данные - сервер прислал некорректный ответ");
        }
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
        validatorProvider.getDataValidator().validateCountLessTypeArgs(
                inputArgs, commandSignature);
    }
}
