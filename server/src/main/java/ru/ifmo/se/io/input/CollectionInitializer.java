package ru.ifmo.se.io.input;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.env.EnvVariableProvider;
import ru.ifmo.se.io.input.exceptions.CollectionInitFromFileException;
import ru.ifmo.se.io.input.fileparser.CsvValidationException;
import ru.ifmo.se.io.input.fileparser.FileParser;
import ru.ifmo.se.io.input.fileprovider.DataProvider;
import ru.ifmo.se.io.output.CollectionActionsMessages;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.logger.AppLogger;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.CreationDateIsAfterNowException;
import ru.ifmo.se.service.exceptions.NonUniqueIdException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.InputFieldValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@RequiredArgsConstructor
public class CollectionInitializer {

    private final EnvVariableProvider envProvider;
    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;
    private final FileParser<Vehicle> initVehiclesParser;
    private final DataProvider dataProvider;
    private final StringFormatter formatter;

    public void initialize() throws IOException {
        AppLogger.LOGGER.info("Приложение запускается");
        String fileName = envProvider.getFileName();

        if (fileName == null) {
            AppLogger.LOGGER.warning("Не найдена переменная окружения с названием файла");
            throw new CollectionInitFromFileException(
                    "Не найдена переменная окружения с именем файла");
        }

        try (InputStreamReader fileScanner =
                     dataProvider.openStreamReader(fileName)) {
            List<Vehicle> vehicles;
            try {
                vehicles = initVehiclesParser.parse(fileScanner);
            } catch (CsvValidationException e) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        "Произошла ошибка инициализации коллекции:", e);
                throw new CollectionInitFromFileException(
                        "Произошла ошибка инициализации коллекции:" + e.getMessage());
            }

            if (checkVehiclesFromFile(vehicles)) {
                if (addAllVehiclesFromFile(vehicles)) {
                    AppLogger.LOGGER.info(
                            "Инициализация коллекции объектами Vehicle из файла завершена успешно");
                } else {
                    collectionService.clear();
                }
            }
        }
    }

    private boolean checkVehiclesFromFile(List<Vehicle> vehicles) {
        int counter = 1;
        for (Vehicle vehicle : vehicles) {

            Set<ConstraintViolation<Vehicle>> violations =
                    validatorProvider.getBeanValidator().validate(vehicle);

            if (!violations.isEmpty()) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        String.format(CollectionActionsMessages.VEHICLE_INIT_VALID_EXC,
                                vehicle.getId(), counter + 1)
                );
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        "Выявленные в нём ошибки: "
                );
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        formatter.formatFieldViolations(violations));
                return false;
            }

            try {
                validatorProvider.getDataValidator()
                        .validateTypedEnginePower(vehicle.getEnginePower());
                validatorProvider.getDataValidator()
                        .validateTypedDistanceTravelled(vehicle.getDistanceTravelled());
            } catch (InputFieldValidationException e) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        String.format(CollectionActionsMessages.VEHICLE_INIT_VALID_EXC,
                                vehicle.getId(), counter + 1)
                );
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        "Выявленная в нём ошибка: " + e.getMessage());
                return false;
            }

            counter++;
        }
        return true;
    }

    private boolean addAllVehiclesFromFile(List<Vehicle> vehicles) {
        int counter = 1;
        for (Vehicle vehicle : vehicles) {
            try {
                if (collectionService.addInitVehicle(vehicle)) {
                    counter++;
                } else {
                    AppLogger.LOGGER.log(
                            Level.SEVERE,
                            String.format(
                                    CollectionActionsMessages.VEHICLE_INIT_UNKNOWN_EXC,
                                    vehicle.getId(), counter + 1
                            )
                    );
                    return false;
                }
            } catch (NonUniqueIdException | CreationDateIsAfterNowException e) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        String.format(
                                CollectionActionsMessages.VEHICLE_INIT_ADD_EXC,
                                vehicle.getId(),
                                counter + 1
                        )
                );
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        "Ошибка добавления в коллекцию: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
