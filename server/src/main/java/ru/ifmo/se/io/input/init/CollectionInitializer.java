package ru.ifmo.se.io.input.init;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.input.exceptions.CollectionInitFromDbException;
import ru.ifmo.se.io.output.CollectionActionsMessages;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.logger.AppLogger;
import ru.ifmo.se.service.CollectionService;
import ru.ifmo.se.service.exceptions.CreationDateIsAfterNowException;
import ru.ifmo.se.service.exceptions.SQLRuntimeException;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.InputFieldValidationException;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

@RequiredArgsConstructor
public class CollectionInitializer {

    private final CollectionService collectionService;
    private final ValidatorProvider validatorProvider;
    private final StringFormatter formatter;

    public void initialize() {
        AppLogger.LOGGER.info("Приложение запускается");

        Collection<Vehicle> vehicles;
        try {
            //collectionService.useMigrate();
            vehicles = collectionService.findAllVehiclesFromDb();

            if (checkVehiclesFromDb(vehicles)) {
                if (addAllVehiclesFromDb(vehicles)) {
                    AppLogger.LOGGER.info(
                            "Инициализация коллекции объектами Vehicle " +
                                    "из базы данных завершена успешно");
                } else {
                    collectionService.clearVehicles();
                }
            }
        } catch (IllegalStateException | SQLRuntimeException e) {
            AppLogger.LOGGER.log(
                    Level.SEVERE,
                    "Произошла ошибка инициализации коллекции:", e);
            throw new CollectionInitFromDbException(
                    "Произошла ошибка инициализации коллекции:" + e.getMessage());
        }
    }

    private boolean checkVehiclesFromDb(Collection<Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {

            Set<ConstraintViolation<Vehicle>> violations =
                    validatorProvider.getBeanValidator().validate(vehicle);

            if (!violations.isEmpty()) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        String.format(CollectionActionsMessages.VEHICLE_INIT_VALID_EXC,
                                vehicle.getId())
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
                                vehicle.getId())
                );
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        "Выявленная в нём ошибка: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private boolean addAllVehiclesFromDb(Collection<Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {
            try {
                if (!collectionService.addInitVehicle(vehicle)) {
                    AppLogger.LOGGER.log(
                            Level.SEVERE,
                            String.format(
                                    CollectionActionsMessages.VEHICLE_INIT_UNKNOWN_EXC,
                                    vehicle.getId()
                            )
                    );
                    return false;
                }
            } catch (SQLRuntimeException | CreationDateIsAfterNowException e) {
                AppLogger.LOGGER.log(
                        Level.SEVERE,
                        String.format(
                                CollectionActionsMessages.VEHICLE_INIT_ADD_EXC,
                                vehicle.getId()
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
