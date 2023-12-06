package ru.practicum.shareit.booking.dto;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemDto;


import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookingDtoReceive {
    @NotNull(message = "ItemId should not be null")
    private long itemId;

    private long bookerId;

    @NotNull(message = "Start date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @FutureOrPresent(message = "Start date and time should not be in past")
    private LocalDateTime start;

    @NotNull(message = "End date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Future(message = "End date and time should be in future")
    private LocalDateTime end;
}
