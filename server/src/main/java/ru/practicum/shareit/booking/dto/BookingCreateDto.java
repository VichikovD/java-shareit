package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingCreateDto {
    private long itemId;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}