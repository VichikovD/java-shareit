package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingInfoDto {
    private long id;

    private ItemInfoDto item;

    private UserDto booker;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;
}
