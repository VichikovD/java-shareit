package ru.practicum.shareit.exception;

// Не понимаю, почему в Postman тестах если владелец пытается забронировать свой предмет нужно выдать ошибку 404 (NOT_FOUND),
// а не 400 (BAD_REQUEST), что можно было бы приурочить к ValidateException
public class OwnerBookingHisItemException extends RuntimeException {
    public OwnerBookingHisItemException(String message) {
        super(message);
    }

    public OwnerBookingHisItemException(String message, Throwable cause) {
        super(message, cause);
    }
}