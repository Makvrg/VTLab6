package ru.ifmo.se.service.exceptions;

public class CreationDateIsAfterNowException extends RuntimeException {

    public CreationDateIsAfterNowException(String message) {
        super(message);
    }
}
