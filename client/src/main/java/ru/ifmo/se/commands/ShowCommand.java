package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseListVehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

public class ShowCommand extends WithoutParametersCommand {

    private final Printer printer;
    private final StringFormatter formatter;

    public ShowCommand(Printer printer,
                       StringFormatter formatter) {
        super("show",
                "вывести в стандартный поток вывода все элементы "
                        + "коллекции в строковом представлении"
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
        if (response instanceof ResponseListVehicle responseListVehicle) {
            if (!responseListVehicle.getVehicleDtoList().isEmpty()) {
                printer.forcePrintln(
                        "Содержимые в коллекции объекты Vehicle:\n"
                                + formatter.formatVehicleCollection(
                                        responseListVehicle.getVehicleDtoList()
                        )
                );
            } else {
                printer.forcePrintln("Коллекция пуста");
            }
        } else {
            printer.forcePrintln(
                    "Не получилось получить данные - сервер прислал некорректный ответ");
        }
    }
}
