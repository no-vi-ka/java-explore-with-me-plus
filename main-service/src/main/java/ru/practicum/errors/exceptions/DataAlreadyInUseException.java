package ru.practicum.errors.exceptions;

public class DataAlreadyInUseException extends RuntimeException {
    public DataAlreadyInUseException(String message) {
        super(message);
    }
}
