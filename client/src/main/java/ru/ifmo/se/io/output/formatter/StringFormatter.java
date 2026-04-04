package ru.ifmo.se.io.output.formatter;

import jakarta.validation.ConstraintViolation;
import ru.ifmo.se.dto.entity.VehicleDto;
import ru.ifmo.se.dto.supporting.CollectionInfoDto;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.output.translator.Translator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringFormatter {

    public String formatCollectionInfoDto(CollectionInfoDto collectionInfoDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Информация о коллекции:\n")
                .append(String.format(
                        "1. Тип коллекции: %s%n",
                        collectionInfoDto.getCollectionType()
                        )
                )
                .append(String.format(
                        "2. Дата инициализации: %s%n",
                        collectionInfoDto.getInitializationDate()
                        )
                )
                .append(String.format(
                        "3. Тип элементов: %s%n",
                        collectionInfoDto.getElementsType()
                        )
                )
                .append(String.format(
                        "4. Количество элементов: %s",
                        collectionInfoDto.getCountOfElements()
                        )
                );
        return sb.toString();
    }

    public String formatHelpMap(Map<String, String> helpMap) {
        StringBuilder helpText = new StringBuilder();
        helpText.append("Справка по командам приложения:\n");
        for (Map.Entry<String, String> pair : helpMap.entrySet()) {
            helpText.append(pair.getKey())
                    .append(" : ")
                    .append(pair.getValue())
                    .append("\n\n");
        }
        helpText.delete(helpText.length() - 2, helpText.length());
        return helpText.toString();
    }

    public String formatVehicle(VehicleDto vehicleDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Объект Vehicle:\n")
          .append("  Id: ").append(vehicleDto.getId()).append("\n")
          .append("  Название: ").append(vehicleDto.getName()).append("\n")
          .append("  Координата x: ").append(vehicleDto.getCoordinates().getX()).append("\n")
          .append("  Координата y: ").append(vehicleDto.getCoordinates().getY()).append("\n")
          .append("  Дата и время сборки: ").append(vehicleDto.getCreationDate()).append("\n")
          .append("  Мощность двигателя: ").append(vehicleDto.getEnginePower()).append("\n")
          .append("  Пробег: ").append(vehicleDto.getDistanceTravelled()).append("\n")
          .append("  Тип транспорта: ")
                .append(Translator.translateToRusOrSelf(vehicleDto.getType().name())).append("\n")
          .append("  Тип топлива: ")
                .append(Translator.translateToRusOrSelf(vehicleDto.getFuelType().name())).append("\n")
          .append("  Имя создателя объекта: ").append(vehicleDto.getUsername());
        return sb.toString();
    }

    public String formatVehicleCollection(Collection<VehicleDto> vehiclesDto) {
        StringBuilder sb = new StringBuilder();
        for (VehicleDto vehicleDto : vehiclesDto) {
            sb.append(formatVehicle(vehicleDto)).append("\n\n");
        }
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    public String formatCurrentReaderInfo(List<Reader> readers) {
        StringBuilder sb = new StringBuilder();
        sb.append("Активен режим чтения ");
        if (readers.size() == 1) {
            sb.append("терминала");
        } else {
            sb.append(
                    String.format(
                            "файла %s",
                            readers.get(readers.size() - 1).getName()
                    )
            );
        }
        return sb.toString();
    }

    public String formatNumberOfGroups(Map<Float, Integer> numberOfGroups) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Float, Integer> entry : numberOfGroups.entrySet()) {
            sb.append(
                    String.format(
                            "Объектов с пробегом=%f в коллекции ровно %d%n",
                            entry.getKey(), entry.getValue()
                    )
            );
        }
        return sb.delete(sb.length() - 1, sb.length()).toString();
    }

    public String formatFieldViolations(Set<? extends ConstraintViolation<?>> fieldViols) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> viol : fieldViols) {
            sb.append(viol.getMessage())
                    .append("\n");
        }
        if (!sb.isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
