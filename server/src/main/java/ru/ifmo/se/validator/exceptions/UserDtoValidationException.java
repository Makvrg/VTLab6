package ru.ifmo.se.validator.exceptions;

public class UserDtoValidationException extends RuntimeException {

    public UserDtoValidationException(String message) {
        super(message);
    }
}
