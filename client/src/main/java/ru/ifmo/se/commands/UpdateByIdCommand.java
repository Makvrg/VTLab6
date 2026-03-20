package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.CoordinatesDto;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicleId;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.ExecuteScriptException;

public class UpdateByIdCommand extends VehicleAwareCommand {

    public UpdateByIdCommand(ValidatorProvider validatorProvider,
                             Printer printer, StringFormatter formatter) {
        super("update id {element}",
                "обновить значение элемента коллекции, id которого равен заданному",
                validatorProvider, printer, formatter
        );
    }

    @Override
    public Request makeRequest(String[] inputArgs, Reader reader) {
        validateArgs(inputArgs);

        this.reader = reader;
        vehicleDto = new VehicleDto();
        vehicleDto.setCoordinates(new CoordinatesDto());
        try {
            readManage();
        } catch (ExecuteScriptException e) {
            printer.forcePrintln(e.getMessage());
            return null;
        }
        return new RequestVehicleId(commandSignature.split(" ")[0],
                vehicleDto, Long.valueOf(inputArgs[0]));
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Ошибка валидации");
            printer.forcePrintln(response.getMessage());
            return;
        }
        printer.forcePrintln("Объект успешно обновлён");
    }

    @Override
    protected void validateArgs(String[] inputArgs) {
        validatorProvider.getDataValidator().validateUpdateByIdArgs(
                inputArgs, commandSignature);
    }
}
