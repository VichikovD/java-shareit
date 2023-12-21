package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookingDto {
    private long id;

    private ItemSendDto item;

    private UserDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
