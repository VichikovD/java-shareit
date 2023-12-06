package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoReceive;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoSend create(BookingDtoReceive bookingDtoReceive);

    BookingDtoSend respondToBooking(long userId, long bookingId, boolean approved);

    BookingDtoSend findBookingById(long userId, long bookingId);

    List<BookingDtoSend> findAllBookingByBookerIdAndState(long bookerId, BookingState state);

    List<BookingDtoSend> findAllBookingByBookerId(long bookerId);

    List<BookingDtoSend> findAllPastBookingByBookerId(long bookerId);

    List<BookingDtoSend> findAllCurrentBookingByBookerId(long bookerId);

    List<BookingDtoSend> findAllFutureBookingByBookerId(long bookerId);

    List<BookingDtoSend> findAllBookingByBookerIdAndStatus(long bookerId, BookingStatus status);

    List<BookingDtoSend> findAllBookingByOwnerIdAndState(long ownerId, BookingState state);

    List<BookingDtoSend> findAllBookingByOwnerId(long ownerId);

    List<BookingDtoSend> findAllPastBookingByOwnerId(long ownerId);

    List<BookingDtoSend> findAllCurrentBookingByOwnerId(long ownerId);

    List<BookingDtoSend> findAllFutureBookingByOwnerId(long ownerId);

    List<BookingDtoSend> findAllBookingByOwnerIdAndStatus(long ownerId, BookingStatus status);
}
