package ru.ifmo.se.io.input.fileparser;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import ru.ifmo.se.entity.Coordinates;
import ru.ifmo.se.validator.ValidatorMessages;

public class CoordinatesConverter extends AbstractBeanField<Coordinates, String> {

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String[] parts = value.trim().split(":");
        if (parts.length != 2) {
            throw new CsvDataTypeMismatchException(
                    ValidatorMessages.COORDS_MUST_BE_IN_FORMAT);
        }

        int x;
        long y;
        try {
            x = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException(
                    ValidatorMessages.X_COORD_MUST_BE_INTEGER);
        }
        try {
            y = Long.parseLong(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException(
                    ValidatorMessages.Y_COORD_MUST_BE_INTEGER);
        }
        return new Coordinates(x, y);
    }
}
