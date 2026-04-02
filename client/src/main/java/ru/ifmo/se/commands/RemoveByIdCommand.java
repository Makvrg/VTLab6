package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.usercontext.UserContext;
import ru.ifmo.se.validator.ValidatorProvider;

public class RemoveByIdCommand extends Command {

    private final ValidatorProvider validatorProvider;
    private final Printer printer;

    public RemoveByIdCommand(ValidatorProvider validatorProvider,
                             Printer printer) {
        super("remove_by_id id", "удалить элемент из коллекции по его id");
        this.validatorProvider = validatorProvider;
        this.printer = printer;
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
        validatorProvider.getDataValidator().validateRemoveByIdArgs(
                inputArgs, getCommandSignature());
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader ignoredReader) {
        validateArgs(inputArgs);
        return new RequestId(
                commandSignature.split(" ")[0],
                Long.valueOf(inputArgs[0]),
                UserContext.getCurrentUser()
        );
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Сервер не смог выполнить команду");
            printer.forcePrintln(response.getMessage());
            return;
        }
        printer.forcePrintln(response.getMessage());
    }
}
