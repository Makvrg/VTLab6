package ru.ifmo.se.io.output.formatter;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.validation.ConstraintViolation;

import java.util.List;
import java.util.Set;

public class StringFormatter {

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

    public String formatFileVehiclesExc(List<CsvException> exceptions) {
        StringBuilder sb = new StringBuilder();
        for (CsvException e : exceptions) {
            sb.append("Ошибка в строке ")
                    .append(e.getLineNumber())
                    .append(": ");

            if (e instanceof CsvDataTypeMismatchException mismatch) {
                sb.append("Поле имеет некорректное значение: ")
                        .append(mismatch.getSourceObject());
            }
            else if (e instanceof CsvRequiredFieldEmptyException required) {
                sb.append("Обязательное поле ")
                        .append(required.getDestinationField().getName())
                        .append(" отсутствует");
            }
            else {
                sb.append(e.getMessage());
            }

            sb.append("\n");
        }
        if (!sb.isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
