package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.service.CollectionService;

public class ExitCommand extends Command {

    private final Printer printer;
    private final CollectionService collectionService;

    public ExitCommand(Printer printer, CollectionService collectionService) {
        super("exit", "завершить программу", false);
        this.printer = printer;
        this.collectionService = collectionService;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        collectionService.exit();
        printer.forcePrintln("Выключение приложения");
        return null;
    }
}
