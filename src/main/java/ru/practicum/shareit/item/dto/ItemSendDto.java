package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

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

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

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
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

}

