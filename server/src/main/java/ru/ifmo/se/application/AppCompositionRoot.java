package ru.ifmo.se.application;

import lombok.Getter;
import ru.ifmo.se.collection.CollectionWithInfo;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.CollectionInitializer;
import ru.ifmo.se.io.input.CommandInvoker;
import ru.ifmo.se.io.input.env.EnvVariableProvider;
import ru.ifmo.se.io.input.fileparser.FileParser;
import ru.ifmo.se.io.input.fileparser.VehicleCsvParser;
import ru.ifmo.se.io.input.fileprovider.DataProvider;
import ru.ifmo.se.io.input.fileprovider.FileProvider;
import ru.ifmo.se.io.output.filewriter.FileWriter;
import ru.ifmo.se.io.output.filewriter.VehicleCsvWriter;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.network.NetworkService;
import ru.ifmo.se.repository.CollectionRepository;
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

    private final CollectionRepository collectionRepository =
            new CollectionRepository(collectionWithInfo);

    @Getter
    private final CollectionService collectionService =
            new CollectionService(collectionRepository);

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

    private final CommandInvoker commandInvoker =
            new CommandInvoker(
                    validatorProvider,
                    collectionService,
                    formatter,
                    csvWriter,
                    envProvider
            );

    @Getter
    private final NetworkService networkService =
            new NetworkService(commandInvoker);
}
