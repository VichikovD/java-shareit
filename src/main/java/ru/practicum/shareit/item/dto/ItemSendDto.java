package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ItemSendDto {
    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Item name should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item name should not be empty")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Item description should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item description should not be empty")
    private String description;

    @NotNull(groups = OnCreate.class, message = "Item available status should not be empty")
    private Boolean available;

    private BookingDtoItem lastBooking;

    private BookingDtoItem nextBooking;

    private List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSendDto itemSendDto = (ItemSendDto) o;
        return Objects.equals(id, itemSendDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    @Builder
    public static class BookingDtoItem {
        private long id;

        private long itemId;

        private long bookerId;

        private LocalDateTime start;

        private LocalDateTime end;

        private BookingStatus status;
    }

    public static BookingDtoItem bookingToBookingDtoItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoItem.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart().toLocalDateTime())
                .end(booking.getEnd().toLocalDateTime())
                .status(booking.getStatus())
                .build();
    }

}
