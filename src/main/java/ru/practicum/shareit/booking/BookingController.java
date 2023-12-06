package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoReceive;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.BookingState;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoSend create(@RequestBody @Validated BookingDtoReceive bookingDtoReceive,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/bookings\" BODY={} , Headers:(X-Sharer-User-Id)={}", bookingDtoReceive, userId);
        bookingDtoReceive.setBookerId(userId);
        BookingDtoSend bookingDtoSend = bookingService.create(bookingDtoReceive);
        log.info(bookingDtoSend.toString());
        return bookingDtoSend;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoSend respondToBooking(@PathVariable long bookingId,
                                           @RequestParam(name = "approved") boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH \"/bookings/{}?approved={}\", Headers:(X-Sharer-User-Id)={}", bookingId, approved, userId);
        BookingDtoSend bookingDtoSend = bookingService.respondToBooking(userId, bookingId, approved);
        log.info(bookingDtoSend.toString());
        return bookingDtoSend;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoSend findBookingById(@PathVariable long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/bookings/{}\", Headers:(X-Sharer-User-Id)={}", bookingId, userId);
        BookingDtoSend bookingDtoSend = bookingService.findBookingById(userId, bookingId);
        log.info(bookingDtoSend.toString());
        return bookingDtoSend;
    }

    @GetMapping
    public List<BookingDtoSend> findAllBookingByBookerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/bookings?state={}\", Headers:(X-Sharer-User-Id)={}", state, userId);
        List<BookingDtoSend> bookingDtoSendList = bookingService.findAllBookingByBookerIdAndState(userId, state);
        log.info(bookingDtoSendList.toString());
        return bookingDtoSendList;
    }

    @GetMapping("/owner")
    public List<BookingDtoSend> findAllBookingByOwnerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/bookings/owner?state={}\", Headers:(X-Sharer-User-Id)={}", state, userId);
        List<BookingDtoSend> bookingDtoSendList = bookingService.findAllBookingByOwnerIdAndState(userId, state);
        log.info(bookingDtoSendList.toString());
        return bookingDtoSendList;
    }
}
