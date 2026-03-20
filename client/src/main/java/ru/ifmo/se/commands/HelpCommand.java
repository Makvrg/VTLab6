package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseHelpMap;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

public class HelpCommand extends WithoutParametersCommand {

    private final Printer printer;
    private final StringFormatter formatter;

    public HelpCommand(Printer printer, StringFormatter formatter) {
        super("help", "вывести справку по доступным командам");
        this.printer = printer;
        this.formatter = formatter;
    }

    @Override
    public void handleResponse(Response response) {
        if (!response.isStatus()) {
            printer.forcePrintln("Сервер не смог подготовить и отправить данные");
            return;
        }
        if (response instanceof ResponseHelpMap responseHelpMap) {
            printer.forcePrintln(
                    formatter.formatHelpMap(
                            responseHelpMap.getHelpMap()
                    )
            );
        } else {
            printer.forcePrintln(
                    "Не получилось получить данные - сервер прислал некорректный ответ");
        }
    }
}
