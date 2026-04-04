package ru.ifmo.se.application;

import lombok.Getter;
import ru.ifmo.se.collection.CollectionWithInfo;
import ru.ifmo.se.db.DbConnectionManager;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.TerminalInputManager;
import ru.ifmo.se.io.input.init.CollectionInitializer;
import ru.ifmo.se.io.input.init.DbMigrator;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.factory.ReaderFactory;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.network.NetworkService;
import ru.ifmo.se.repository.DataRepository;
import ru.ifmo.se.repository.DbRepository;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class AppCompositionRoot {

    private final Collection<Vehicle> collection =
            Collections.synchronizedSet(new HashSet<>());

    private final CollectionWithInfo collectionWithInfo =
            new CollectionWithInfo(
                    collection,
                    collection.getClass(),
                    Vehicle.class
            );

    private final ValidatorProvider validatorProvider = new ValidatorProvider();

    private final StringFormatter formatter =
            new StringFormatter();

    @Getter
    private final DbConnectionManager connectionManager =
            new DbConnectionManager();
    private final DbRepository dbRepository = new DbRepository(connectionManager);
    private final DataRepository dataRepository =
            new DataRepository(collectionWithInfo, dbRepository);
    private final DbMigrator migrator = new DbMigrator();

    @Getter
    private final CollectionService collectionService =
            new CollectionService(dataRepository, connectionManager, migrator);

    @Getter
    private final CollectionInitializer collectionInitializer =
            new CollectionInitializer(
                    collectionService,
                    validatorProvider,
                    formatter
            );

    private final Printer printer = new Printer();

    private final CommandInvoker commandInvoker =
            new CommandInvoker(
                    validatorProvider,
                    collectionService,
                    formatter,
                    printer
            );

    private final ReaderFactory readerFactory = new ReaderFactory();
    private final Reader reader =
            readerFactory.createTerminalReader("Main Terminal");

    private final TerminalInputManager terminalInputManager =
            new TerminalInputManager(reader, printer, formatter);
    @Getter
    private final TerminalPipeline terminalPipeline =
            new TerminalPipeline(printer, terminalInputManager, commandInvoker);

    @Getter
    private final NetworkService networkService =
            new NetworkService(commandInvoker);
}
