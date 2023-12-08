package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReceiveDto;
import ru.practicum.shareit.booking.state.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingReceiveDto bookingReceiveDto);

    BookingDto respondToBooking(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long userId, long bookingId);

    List<BookingDto> findAllBookingByBookerIdAndState(long bookerId, BookingState state);

    List<BookingDto> findAllBookingByOwnerIdAndState(long ownerId, BookingState state);
}
