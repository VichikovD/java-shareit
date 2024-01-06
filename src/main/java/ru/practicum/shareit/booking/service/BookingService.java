package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.state.BookingState;

import java.util.List;

public interface BookingService {
    BookingInfoDto create(BookingCreateDto bookingCreateDto);

    BookingInfoDto respondToBooking(long userId, long bookingId, boolean approved);

    BookingInfoDto findBookingById(long userId, long bookingId);

    List<BookingInfoDto> findAllBookingByBookerIdAndState(long bookerId, BookingState state, Pageable pageable);

    List<BookingInfoDto> findAllBookingByOwnerIdAndState(long ownerId, BookingState state, Pageable pageable);
}
