package ru.ifmo.se.io.input.fileparser;

public class CsvValidationException extends RuntimeException {

    public CsvValidationException(String message) {
        super(message);
    }

    public CsvValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
