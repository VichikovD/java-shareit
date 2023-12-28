package ru.practicum.shareit.booking.Dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoTest {
    private static final LocalDateTime CREATED = LocalDateTime.now();
    @Test
    public void bookingCreateDto() {
        BookingCreateDto bookingCreateDto1 = BookingCreateDto.builder()
                .itemId(1L)
                .bookerId(1L)
                .start(CREATED.plusDays(1))
                .end(CREATED.plusDays(2))
                .build();

        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(1L, 1L, CREATED.plusDays(1), CREATED.plusDays(2));
        assertEquals(bookingCreateDto1, bookingCreateDto2);
    }

    /*@Test
    public void bookingInfoDto() {
        BookingInfoDto bookingCreateDto1 = BookingInfoDto.builder()
                .id(1L)
                .item(null)
                .booker(null)
                .start(CREATED.plusDays(1))
                .end(CREATED.plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingInfoDto bookingCreateDto2 = new BookingInfoDto(1L, null, null, CREATED.plusDays(1), CREATED.plusDays(2), BookingStatus.WAITING);
        assertEquals(bookingCreateDto1, bookingCreateDto2);
    }*/


}
