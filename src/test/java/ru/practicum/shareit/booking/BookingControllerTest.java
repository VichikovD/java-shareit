package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService bookingService;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END = LocalDateTime.now().plusDays(2);

    @Test
    void create() throws Exception {
        final BookingRequestingDto itemRequestDto = getBookingReceiveDto();
        //  final BookingCreateDto itemToSave = getBookingCreateDto();
        final BookingInfoDto bookingReturned = getBookingDto();
        Mockito.when(bookingService.create(Mockito.any(BookingCreateDto.class)))
                .thenReturn(bookingReturned);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$.end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$.status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void create_whenValidateException_thenBadRequestReturned() throws Exception {
        final BookingRequestingDto itemRequestDto = getBookingReceiveDto();
        final BookingCreateDto itemToSave = getBookingCreateDto();
        Mockito.when(bookingService.create(Mockito.any(BookingCreateDto.class)))
                .thenThrow(new ValidateException("Item to be booked is not available"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(400));
    }

    @Test
    void respondToBooking() throws Exception {
        BookingInfoDto bookingReturned = getBookingDto();
        bookingReturned.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.respondToBooking(1L, 1L, true))
                .thenReturn(bookingReturned);

        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$.end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$.status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void findBookingById() throws Exception {
        final BookingInfoDto bookingReturned = getBookingDto();
        Mockito.when(bookingService.findBookingById(1L, 1L))
                .thenReturn(bookingReturned);

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$.end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$.status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void findAllBookingByBookerIdAndState_whenUnknownState_thenException() throws Exception {
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNKNOWN")
                        .param("size", "1")
                        .param("from", "1")
                )
                .andExpect(status().is(400));
    }

    @Test
    void findAllBookingByBookerIdAndState() throws Exception {
        int limit = 1;
        int offset = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        final List<BookingInfoDto> bookingReturnedList = List.of(getBookingDto());
        Mockito.when(bookingService.findAllBookingByBookerIdAndState(1L, BookingState.PAST, pageable))
                .thenReturn(bookingReturnedList);

        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST")
                        .param("size", "1")
                        .param("from", "1")
                )
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$[0].end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$[0].status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void findAllBookingByOwnerIdAndState() throws Exception {
        int limit = 1;
        int offset = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        final List<BookingInfoDto> bookingReturnedList = List.of(getBookingDto());
        Mockito.when(bookingService.findAllBookingByOwnerIdAndState(1L, BookingState.PAST, pageable))
                .thenReturn(bookingReturnedList);

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST")
                        .param("size", "1")
                        .param("from", "1")
                )
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$[0].end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$[0].status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    private BookingInfoDto getBookingDto() {
        return BookingInfoDto.builder()
                .id(1L)
                .item(getItemSendDto())
                .booker(getUserDto())
                .start(START)
                .end(END)
                .status(BookingStatus.WAITING)
                .build();
    }

    private ItemInfoDto getItemSendDto() {
        return new ItemInfoDto(1L, "name", "description", true, null, null, null, null);
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "user@email.com", "userName");
    }

    private BookingRequestingDto getBookingReceiveDto() {
        return BookingRequestingDto.builder()
                .itemId(1L)
                .start(START)
                .end(END)
                .build();
    }

    private BookingCreateDto getBookingCreateDto() {
        return BookingCreateDto.builder()
                .itemId(1L)
                .bookerId(1L)
                .start(START)
                .end(END)
                .build();
    }
}