package ru.practicum.errors.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataAlreadyInUseException extends RuntimeException {
    public DataAlreadyInUseException(String message) {
        super(message);
    }
}
