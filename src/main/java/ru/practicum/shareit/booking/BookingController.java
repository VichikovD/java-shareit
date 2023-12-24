package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReceiveDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.BookingState;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@Validated
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody @Validated BookingReceiveDto bookingReceiveDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/bookings\" BODY={} , Headers:(X-Sharer-User-Id)={}", bookingReceiveDto, userId);
        bookingReceiveDto.setBookerId(userId);
        BookingDto bookingDto = bookingService.create(bookingReceiveDto);
        log.info(bookingDto.toString());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto respondToBooking(@PathVariable long bookingId,
                                       @RequestParam(name = "approved") boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH \"/bookings/{}?approved={}\", Headers:(X-Sharer-User-Id)={}", bookingId, approved, userId);
        BookingDto bookingDto = bookingService.respondToBooking(userId, bookingId, approved);
        log.info(bookingDto.toString());
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/bookings/{}\", Headers:(X-Sharer-User-Id)={}", bookingId, userId);
        BookingDto bookingDto = bookingService.findBookingById(userId, bookingId);
        log.info(bookingDto.toString());
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllBookingByBookerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int limit,
                                                             @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int offset) {
        log.info("GET \"/bookings?state={}&from={}&size={}\", Headers:(X-Sharer-User-Id)={}", state, offset, limit, userId);
        List<BookingDto> bookingDtoList = bookingService.findAllBookingByBookerIdAndState(userId, state, limit, offset);
        log.info(bookingDtoList.toString());
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingByOwnerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                            @RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int limit,
                                                            @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int offset) {
        log.info("GET \"/bookings/owner?state={}\", Headers:(X-Sharer-User-Id)={}", state, userId);
        List<BookingDto> bookingDtoList = bookingService.findAllBookingByOwnerIdAndState(userId, state, limit, offset);
        log.info(bookingDtoList.toString());
        return bookingDtoList;
    }
}
