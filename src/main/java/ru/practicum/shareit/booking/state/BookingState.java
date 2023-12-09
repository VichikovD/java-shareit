package ru.practicum.shareit.booking.state;

import ru.practicum.shareit.exception.UnsupportedStateException;

public enum BookingState {
    ALL, PAST, CURRENT, FUTURE, WAITING, REJECTED;

    public static BookingState fromString(String string) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.toString().equals(string.toUpperCase())) {
                return bookingState;
            }
        }
        throw new UnsupportedStateException("Unknown state: " + string);
    }
}
