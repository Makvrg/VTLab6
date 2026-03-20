package ru.ifmo.se.commands;

import ru.ifmo.se.dto.request.Request;
import ru.ifmo.se.dto.response.Response;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.env.EnvVariableProvider;
import ru.ifmo.se.io.output.CollectionActionsMessages;
import ru.ifmo.se.io.output.filewriter.FileWriter;
import ru.ifmo.se.logger.AppLogger;
import ru.ifmo.se.service.CollectionService;

import java.io.IOException;
import java.util.logging.Level;

public class SaveCommand extends Command {

    private final CollectionService collectionService;
    private final FileWriter<Vehicle> fileWriter;
    private final EnvVariableProvider envProvider;

    public SaveCommand(CollectionService collectionService,
                       FileWriter<Vehicle> fileWriter,
                       EnvVariableProvider envProvider) {
        super("save", "сохранить коллекцию в файл", false);
        this.collectionService = collectionService;
        this.fileWriter = fileWriter;
        this.envProvider = envProvider;
    }

    @Override
    public Response execute(Request ignoredRequest) {
        String fileName = envProvider.getFileName();
        try {
            if (fileName != null) {
                fileWriter.write(fileName, collectionService.getVehiclesForSave());
                AppLogger.LOGGER.info(String.format(
                        "Сохранение коллекции в файл %s прошло успешно",
                        fileName
                        )
                );
                return null;
            } else {
                fileWriter.writeBackup(collectionService.getVehiclesForSave());
                AppLogger.LOGGER.info("Сохранение коллекции в запасной файл прошло успешно");
                return null;
            }
        } catch (IOException e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    CollectionActionsMessages.SAVE_COLLECTION_EXC
            );
            return null;
        }
    }
}
