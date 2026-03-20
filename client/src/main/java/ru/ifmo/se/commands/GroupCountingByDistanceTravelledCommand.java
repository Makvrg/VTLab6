package ru.ifmo.se.commands;

import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.dto.response.ResponseNumberOfGroups;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

public class GroupCountingByDistanceTravelledCommand extends WithoutParametersCommand {

    private final Printer printer;
    private final StringFormatter formatter;

    public GroupCountingByDistanceTravelledCommand(Printer printer,
                                                   StringFormatter formatter) {
        super("group_counting_by_distance_travelled",
                "сгруппировать элементы коллекции по значению поля distanceTravelled, " +
                        "вывести количество элементов в каждой группе"
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
        if (response instanceof ResponseNumberOfGroups responseGroups) {
            if (!responseGroups.getNumberOfGroups().isEmpty()) {
                printer.forcePrintln(
                        formatter.formatNumberOfGroups(
                                responseGroups.getNumberOfGroups()
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
