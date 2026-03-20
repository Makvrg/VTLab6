package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseCollectionInfo;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

public class InfoCommand extends WithoutParametersCommand {

    private final Printer printer;
    private final StringFormatter formatter;

    public InfoCommand(Printer printer, StringFormatter formatter) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции "
                + "(тип, дата инициализации, тип и количество элементов)"
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
        if (response instanceof ResponseCollectionInfo responseCollectionInfo) {
            printer.forcePrintln(
                    formatter.formatCollectionInfoDto(
                            responseCollectionInfo.getCollectionInfoDto()
                    )
            );
        } else {
            printer.forcePrintln(
                    "Не получилось получить данные - сервер прислал некорректный ответ");
        }
    }
}
