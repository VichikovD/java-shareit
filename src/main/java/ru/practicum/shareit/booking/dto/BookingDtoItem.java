package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

// чтобы не возникало проблемы рекурсии в поле "Item item" при добавлении в класс Item
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookingDtoItem {
    private long id;

    private long itemId;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
