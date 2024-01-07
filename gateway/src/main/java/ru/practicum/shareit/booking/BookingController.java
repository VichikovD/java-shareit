package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Validated BookingRequestingDto requestDto) {
        log.info("POST \"/bookings\", Body={}, Headers:(X-Sharer-User-Id)={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> respondToBooking(@PathVariable long bookingId,
                                                   @RequestParam(name = "approved") boolean approved,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH \"/bookings/{}?approved={}\", Headers:(X-Sharer-User-Id)={}", bookingId, approved, userId);
        ResponseEntity<Object> bookingInfoDto = bookingClient.respondToBooking(userId, bookingId, approved);
        log.info(bookingInfoDto.toString());
        return bookingInfoDto;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long bookingId) {
        log.info("GET \"/bookings/{}\", Headers:(X-Sharer-User-Id)={}", bookingId, userId);
        ResponseEntity<Object> booking = bookingClient.findBookingById(userId, bookingId);
        log.info(booking.toString());
        return booking;
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET \"/bookings?state={}&from={}&size={}\", Headers:(X-Sharer-User-Id)={}", stateParam, from, size, userId);
        ResponseEntity<Object> bookingList = bookingClient.findAllBookingByBookerIdAndState(userId, state, from, size);
        log.info(bookingList.toString());
        return bookingList;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingByOwnerIdAndState(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int size,
                                                                  @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int from) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET \"/bookings/owner?state={}\", Headers:(X-Sharer-User-Id)={}", state, userId);
        ResponseEntity<Object> bookingInfoDtoList = bookingClient.findAllBookingByOwnerIdAndState(userId, state, from, size);
        log.info(bookingInfoDtoList.toString());
        return bookingInfoDtoList;
    }
}
