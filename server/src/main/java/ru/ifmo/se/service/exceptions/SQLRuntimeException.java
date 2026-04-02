package ru.ifmo.se.service.exceptions;

public class SQLRuntimeException extends RuntimeException {

    public SQLRuntimeException(String message) {
        super(message);
    }
}
