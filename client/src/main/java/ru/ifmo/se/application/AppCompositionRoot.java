package ru.ifmo.se.application;

import lombok.Getter;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.InputManager;
import ru.ifmo.se.io.input.exceptions.IORuntimeException;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.factory.ReaderFactory;
import ru.ifmo.se.io.input.readers.file.DataProvider;
import ru.ifmo.se.io.input.readers.file.FileProvider;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.network.NetworkService;
import ru.ifmo.se.validator.ValidatorProvider;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public final class AppCompositionRoot {

    private final Printer printer = new Printer();

    private final StringFormatter formatter = new StringFormatter();

    private final ReaderFactory readerFactory = new ReaderFactory();
    private final Reader reader =
            readerFactory.createTerminalReader("Main Terminal");

    private final ValidatorProvider validatorProvider = new ValidatorProvider();

    private final DataProvider dataProvider =
            new FileProvider();

    private final InputManager inputManager = new InputManager(
            reader,
            printer,
            formatter
    );

    private final String ip = "127.0.0.1";
    private final int port = 56789;
    private final DatagramChannel channel;
    private final Selector selector;
    {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.exit(1);
            throw new IORuntimeException();
        }
    }

    private final NetworkService networkService =
            new NetworkService(ip, port, channel, selector);

    @Getter
    private final CommandInvoker commandInvoker =
            new CommandInvoker(dataProvider, readerFactory, validatorProvider,
                    inputManager.getReaders(), formatter, printer
            );

    @Getter
    private final Pipeline pipeline =
            new Pipeline(printer, inputManager, commandInvoker, networkService);
}
