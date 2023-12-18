package ru.practicum.shareit.exception;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }

    public LockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
