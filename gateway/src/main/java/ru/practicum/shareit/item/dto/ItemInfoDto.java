/*
package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemInfoDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentInfoDto> comments;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class BookingDto {
        private long id;

        private long itemId;

        private long bookerId;

        private LocalDateTime start;

        private LocalDateTime end;

        private BookingStatus status;
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

}

*/
