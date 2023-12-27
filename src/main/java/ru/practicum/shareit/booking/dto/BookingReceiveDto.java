package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.validation.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@StartBeforeEnd
public class BookingReceiveDto {
    @NotNull(message = "ItemId should not be null")
    private long itemId;

    private Long bookerId;

    @NotNull(message = "Start date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @FutureOrPresent(message = "Start date and time should not be in past")
    private LocalDateTime start;

    @NotNull(message = "End date-time should not be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @Future(message = "End date and time should be in future")
    private LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingReceiveDto that = (BookingReceiveDto) o;
        return itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
}
