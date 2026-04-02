package ru.ifmo.se.application;

import lombok.Getter;
import ru.ifmo.se.collection.CollectionWithInfo;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.CollectionInitializer;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.TerminalInputManager;
import ru.ifmo.se.io.input.env.EnvVariableProvider;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.factory.ReaderFactory;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.network.NetworkService;
import ru.ifmo.se.repository.DataRepository;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.validator.ValidatorProvider;

import java.util.Collection;
import java.util.HashSet;

public final class AppCompositionRoot {

    private final Collection<Vehicle> collection = new HashSet<>();

    private final CollectionWithInfo collectionWithInfo =
            new CollectionWithInfo(
                    collection,
                    collection.getClass(),
                    Vehicle.class
            );

    private final ValidatorProvider validatorProvider = new ValidatorProvider();

    private final StringFormatter formatter =
            new StringFormatter();

    private static final String ENV_VAR_NAME = "VEHICLE_FILE";
    private static final String BACKUP_FILE_NAME =
            "backup_collection_save_file_wl64fI983T";
    private final EnvVariableProvider envProvider =
            new EnvVariableProvider(ENV_VAR_NAME);
    private final FileWriter<Vehicle> csvWriter =
            new VehicleCsvWriter(BACKUP_FILE_NAME);
    private final DataProvider dataProvider =
            new FileProvider();
    private final FileParser<Vehicle> csvParser =
            new VehicleCsvParser(formatter);

    private final DataRepository dataRepository =
            new DataRepository(collectionWithInfo);

    @Getter
    private final CollectionService collectionService =
            new CollectionService(dataRepository);

    @Getter
    private final CollectionInitializer collectionInitializer =
            new CollectionInitializer(
                    envProvider,
                    collectionService,
                    validatorProvider,
                    csvParser,
                    dataProvider,
                    formatter
            );

    private final Printer printer = new Printer();

    private final CommandInvoker commandInvoker =
            new CommandInvoker(
                    validatorProvider,
                    collectionService,
                    formatter,
                    csvWriter,
                    envProvider,
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
