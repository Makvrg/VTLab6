package ru.ifmo.se.service.exceptions;

public class RemoveByIdIllegalStateException extends RuntimeException {

    public RemoveByIdIllegalStateException(String message) {
        super(message);
    }
}
