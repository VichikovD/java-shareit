package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingRequestingDto {
    //  Requesting чтобы при создании dto для Item, не получился ItemRequestDto,
    //  который можно спутать с сущностью ItemRequest
    private long itemId;

    private LocalDateTime start;

    private LocalDateTime end;

}
