package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookingDtoSend {
    private long id;

    private ItemDto item;

    private UserDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
