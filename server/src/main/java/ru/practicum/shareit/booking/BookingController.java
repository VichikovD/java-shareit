package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestingDto;
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
    public BookingInfoDto create(@RequestBody @Validated BookingRequestingDto bookingRequestingDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/bookings\" BODY={} , Headers:(X-Sharer-User-Id)={}", bookingRequestingDto, userId);
        BookingCreateDto bookingCreateDto = BookingMapper.toCreateDto(bookingRequestingDto, userId);
        BookingInfoDto bookingInfoDto = bookingService.create(bookingCreateDto);
        log.info(bookingInfoDto.toString());
        return bookingInfoDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto respondToBooking(@PathVariable long bookingId,
                                           @RequestParam(name = "approved") boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH \"/bookings/{}?approved={}\", Headers:(X-Sharer-User-Id)={}", bookingId, approved, userId);
        BookingInfoDto bookingInfoDto = bookingService.respondToBooking(userId, bookingId, approved);
        log.info(bookingInfoDto.toString());
        return bookingInfoDto;
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto findBookingById(@PathVariable long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/bookings/{}\", Headers:(X-Sharer-User-Id)={}", bookingId, userId);
        BookingInfoDto bookingInfoDto = bookingService.findBookingById(userId, bookingId);
        log.info(bookingInfoDto.toString());
        return bookingInfoDto;
    }

    @GetMapping
    public List<BookingInfoDto> findAllBookingByBookerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                                                 @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int limit,
                                                                 @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int offset) {
        log.info("GET \"/bookings?state={}&from={}&size={}\", Headers:(X-Sharer-User-Id)={}", state, offset, limit, userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<BookingInfoDto> bookingInfoDtoList = bookingService.findAllBookingByBookerIdAndState(userId, state, pageable);
        log.info(bookingInfoDtoList.toString());
        return bookingInfoDtoList;
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> findAllBookingByOwnerIdAndState(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int limit,
                                                                @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int offset) {
        log.info("GET \"/bookings/owner?state={}\", Headers:(X-Sharer-User-Id)={}", state, userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<BookingInfoDto> bookingInfoDtoList = bookingService.findAllBookingByOwnerIdAndState(userId, state, pageable);
        log.info(bookingInfoDtoList.toString());
        return bookingInfoDtoList;
    }
}
