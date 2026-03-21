package ru.ifmo.se.commands;

import ru.ifmo.se.dto.entity.CoordinatesDto;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.request.RequestVehicle;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.input.exceptions.InputArgsValidationException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.ExecuteScriptException;

public abstract class RemoveByCompareCommand extends VehicleAwareCommand {

    protected RemoveByCompareCommand(
            String commandSignature,
            String commandDescription,
            ValidatorProvider validatorProvider,
            Printer printer,
            StringFormatter formatter) {
        super(commandSignature, commandDescription,
                validatorProvider, printer, formatter);
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
            throw e;
        }
        return new RequestVehicle(commandSignature.split(" ")[0], vehicleDto);
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Ошибка валидации");
            printer.forcePrintln(response.getMessage());
            return;
        }
        printer.forcePrintln(response.getMessage());
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
