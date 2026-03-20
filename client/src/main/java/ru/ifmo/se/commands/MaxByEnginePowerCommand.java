package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseVehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

public class MaxByEnginePowerCommand extends WithoutParametersCommand {

    private final Printer printer;
    private final StringFormatter formatter;

    public MaxByEnginePowerCommand(Printer printer,
                                   StringFormatter formatter) {
        super("max_by_engine_power",
                "вывести любой объект из коллекции, " +
                        "значение поля enginePower которого является максимальным"
        );
        this.printer = printer;
        this.formatter = formatter;
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Сервер не смог подготовить и отправить данные");
            return;
        }
        if (response instanceof ResponseVehicle responseVehicle) {
            if (responseVehicle.getVehicleDto() != null) {
                printer.forcePrintln("Найденный объект:\n"
                        + formatter.formatVehicle(responseVehicle.getVehicleDto()));
            } else {
                printer.forcePrintln("Коллекция пуста");
            }
        } else {
            printer.forcePrintln(
                    "Не получилось получить данные - сервер прислал некорректный ответ");
        }
    }
}
