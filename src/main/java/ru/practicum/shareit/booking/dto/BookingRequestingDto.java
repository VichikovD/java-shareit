package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.validation.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@StartBeforeEnd
public class BookingRequestingDto {
    //  Requesting чтобы при создании dto для Item, не получился ItemRequestDto,
    //  который можно спутать с сущностью ItemRequest
    @NotNull(message = "ItemId should not be null")
    private long itemId;

    @NotNull(message = "Start date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @FutureOrPresent(message = "Start date and time should not be in past")
    private LocalDateTime start;

    @NotNull(message = "End date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Future(message = "End date and time should be in future")
    private LocalDateTime end;

}
