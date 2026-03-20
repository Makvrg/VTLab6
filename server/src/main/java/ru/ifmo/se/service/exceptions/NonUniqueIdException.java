package ru.ifmo.se.service.exceptions;

public class NonUniqueIdException extends RuntimeException {

    public NonUniqueIdException(String message) {
        super(message);
    }
}
