package ru.ifmo.se.commands;

import jakarta.validation.ConstraintViolation;
import ru.ifmo.se.dto.entity.CoordinatesDto;
import ru.ifmo.se.dto.entity.FuelTypeDto;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.entity.VehicleTypeDto;
import ru.ifmo.se.dto.entity.fieldnames.VehicleFieldNames;
import ru.ifmo.se.io.input.readers.InputTextHandler;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.file.FileReader;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;
import ru.ifmo.se.io.output.translator.Translator;
import ru.ifmo.se.validator.ValidatorProvider;
import ru.ifmo.se.validator.exceptions.ExecuteScriptException;
import ru.ifmo.se.validator.exceptions.InputFieldValidationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class VehicleAwareCommand extends Command {

    protected Reader reader;

    protected final ValidatorProvider validatorProvider;
    protected final Printer printer;
    private final StringFormatter formatter;
    private final Map<String, Supplier<String>> readActions = buildMapOfReadActions();
    private boolean inputIsRepeated = false;

    protected VehicleDto vehicleDto;

    private final Map<String, Consumer<String>> inputValidateMethods;
    private final Map<String, Consumer<String>> vehicleFieldSetters;

    protected VehicleAwareCommand(
            String commandSignature,
            String commandDescription,
            ValidatorProvider validatorProvider,
            Printer printer,
            StringFormatter formatter) {
        super(commandSignature, commandDescription);
        this.validatorProvider = validatorProvider;
        this.printer = printer;
        this.formatter = formatter;
        vehicleDto = new VehicleDto();
        vehicleDto.setCoordinates(new CoordinatesDto());
        inputValidateMethods = buildMapOfInputTypeValidateMethods();
        vehicleFieldSetters = buildMapOfDtoFieldSetters();
    }

    protected void readManage() {
        printer.printlnIfOn("Следуя указаниям, введите данные объекта Vehicle");
        for (Map.Entry<String, Supplier<String>> actionEntry : readActions.entrySet()) {
            boolean valid = false;
            while (!valid) {
                String field = actionEntry.getKey();
                String input = actionEntry.getValue().get();
                try {
                    fieldMange(field, input);
                    printer.printlnIfOn("");
                    inputIsRepeated = false;
                    valid = true;
                } catch (InputFieldValidationException e) {
                    printer.printlnIfOn("\n" + e.getMessage() + ", повторите ввод");
                    inputIsRepeated = true;
                }
            }
        }
    }

    private void fieldMange(String field, String input) {
        Consumer<String> validateMethod = inputValidateMethods.get(field);
        if (validateMethod != null) {
            validateMethod.accept(input);
        }

        vehicleFieldSetters.get(field).accept(input);

        String fieldPath;
        if (field.equals(VehicleFieldNames.X.getTitle()) ||
                field.equals(VehicleFieldNames.Y.getTitle())) {
            fieldPath = "coordinates." + field;
        } else {
            fieldPath = field;
        }
        Set<? extends ConstraintViolation<?>> fieldViols =
                validatorProvider.getBeanValidator()
                        .validateProperty(vehicleDto, fieldPath);
        if (!fieldViols.isEmpty()) {
            throw new InputFieldValidationException(
                    formatter.formatFieldViolations(fieldViols));
        }
    }

    private String readInput(String explanation,
                             String message) {
        if (explanation != null && !inputIsRepeated) {
            printer.printlnIfOn(explanation);
        }
        printer.printIfOn(message + " > ");
        try {
            String inputString = reader.readLine();
            if (inputString == null) {
                if (reader instanceof FileReader) {
                    throw new ExecuteScriptException(
                            "Неожиданное количество строк данных в файле"
                    );
                }
                inputString = "";
                printer.forcePrintln("");
            }
            return InputTextHandler.stripOrNullField(inputString);
        } catch (IOException e) {
            throw new ExecuteScriptException(
                    "Файл с указанным названием не найден или к нему нет доступа"
            );
        }
    }

    private Map<String, Supplier<String>> buildMapOfReadActions() {
        Map<String, Supplier<String>> actions = new LinkedHashMap<>();

        actions.put(VehicleFieldNames.NAME.getTitle(),
                () ->
                        readInput(
                                null,
                                "Введите название транспорта"
                        )
        );
        actions.put(VehicleFieldNames.X.getTitle(),
                () ->
                        readInput(
                                "x-координата транспорта - целое число больше -482",
                                "Введите координату x"
                        )
        );
        actions.put(VehicleFieldNames.Y.getTitle(),
                () ->
                        readInput(
                                "y-координата транспорта - целое число",
                                "Введите координату y"
                        )
        );
        actions.put(VehicleFieldNames.ENGINE_POWER.getTitle(),
                () ->
                        readInput(
                                "Мощность двигателя - вещественное число больше 0",
                                "Введите мощность двигателя"
                        )
        );
        actions.put(VehicleFieldNames.DISTANCE_TRAVELLED.getTitle(),
                () ->
                        readInput(
                                "Пробег - вещественное число больше 0",
                                "Введите пробег транспорта"
                        )
        );
        StringBuilder typeExplanation = new StringBuilder();
        typeExplanation.append("Возможные типы транспорта:\n");
        for (VehicleTypeDto vehicleType : VehicleTypeDto.values()) {
            typeExplanation.append("- ")
                    .append(Translator.translateToRusOrSelf(vehicleType.name()))
                    .append("\n");
        }
        actions.put(VehicleFieldNames.TYPE.getTitle(),
                () ->
                        readInput(
                                typeExplanation.toString(),
                                "Введите тип транспортного средства"
                        )
        );
        StringBuilder fuelTypeExplanation = new StringBuilder();
        fuelTypeExplanation.append("Возможные типы энергии двигателя:\n");
        for (FuelTypeDto fuelType : FuelTypeDto.values()) {
            fuelTypeExplanation.append("- ")
                    .append(Translator.translateToRusOrSelf(fuelType.name()))
                    .append("\n");
        }
        actions.put(VehicleFieldNames.FUEL_TYPE.getTitle(),
                () ->
                        readInput(
                                fuelTypeExplanation.toString(),
                                "Введите тип энергии двигателя"
                        )
        );
        return actions;
    }

    private Map<String, Consumer<String>> buildMapOfInputTypeValidateMethods() {
        Map<String, Consumer<String>> validateMethods = new HashMap<>();

        validateMethods.put(
                VehicleFieldNames.X.getTitle(),
                validatorProvider.getDataValidator()::validateXCoordType);
        validateMethods.put(
                VehicleFieldNames.Y.getTitle(),
                validatorProvider.getDataValidator()::validateYCoordType);
        validateMethods.put(
                VehicleFieldNames.ENGINE_POWER.getTitle(),
                validatorProvider.getDataValidator()::validateEnginePowerType);
        validateMethods.put(
                VehicleFieldNames.DISTANCE_TRAVELLED.getTitle(),
                validatorProvider.getDataValidator()::validateDistanceTravelledType);
        validateMethods.put(
                VehicleFieldNames.TYPE.getTitle(),
                validatorProvider.getDataValidator()::validateRusVehicleType
        );
        validateMethods.put(
                VehicleFieldNames.FUEL_TYPE.getTitle(),
                validatorProvider.getDataValidator()::validateRusFuelType
        );
        return validateMethods;
    }

    private Map<String, Consumer<String>> buildMapOfDtoFieldSetters() {
        Map<String, Consumer<String>> setters = new HashMap<>();

        setters.put(VehicleFieldNames.NAME.getTitle(), name -> vehicleDto.setName(name));
        setters.put(VehicleFieldNames.X.getTitle(),
                x -> vehicleDto.getCoordinates().setX(
                        (x != null) ? Integer.valueOf(x) : null
                )
        );
        setters.put(VehicleFieldNames.Y.getTitle(),
                y -> vehicleDto.getCoordinates().setY(Long.parseLong(y)));
        setters.put(VehicleFieldNames.ENGINE_POWER.getTitle(),
                pow -> vehicleDto.setEnginePower(Double.parseDouble(pow)));
        setters.put(VehicleFieldNames.DISTANCE_TRAVELLED.getTitle(),
                dist -> vehicleDto.setDistanceTravelled(Float.valueOf(dist)));
        setters.put(VehicleFieldNames.TYPE.getTitle(),
                type ->
                        vehicleDto.setType(
                                VehicleTypeDto.valueOf(Translator.translateToEngOrSelf(type))
                        )
        );
        setters.put(VehicleFieldNames.FUEL_TYPE.getTitle(),
                fuel ->
                        vehicleDto.setFuelType(
                                FuelTypeDto.valueOf(Translator.translateToEngOrSelf(fuel))
                        )
        );
        return setters;
    }
}
