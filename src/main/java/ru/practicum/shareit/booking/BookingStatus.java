package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELLED;

    public static BookingStatus getBookingStatusByBoolean(boolean approved) {
        if (approved) {
            return BookingStatus.APPROVED;
        } else {
            return BookingStatus.REJECTED;
        }
    }
}
