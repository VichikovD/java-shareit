package ru.practicum.shareit.exception;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(String message) {
        super(message);
    }

    public UnsupportedStateException(String message, Throwable cause) {
        super(message, cause);
    }
}