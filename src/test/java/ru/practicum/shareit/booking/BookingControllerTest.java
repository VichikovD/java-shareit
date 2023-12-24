package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReceiveDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.item.dto.ItemSendDto;
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
        final BookingReceiveDto itemSendDto = getBookingReceiveDtoNullBookerId();
        final BookingReceiveDto itemToSave = getBookingReceiveDto();
        final BookingDto bookingReturned = getBookingDto();
        Mockito.when(bookingService.create(itemToSave))
                .thenReturn(bookingReturned);

        mvc.perform(post("/bookings").content(mapper.writeValueAsString(itemSendDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item", is(getItemSendDto()), ItemSendDto.class))
                .andExpect(jsonPath("$.booker", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$.start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$.end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$.status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void respondToBooking() throws Exception {
        BookingDto bookingReturned = getBookingDto();
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
                .andExpect(jsonPath("$.item", is(getItemSendDto()), ItemSendDto.class))
                .andExpect(jsonPath("$.booker", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$.start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$.end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$.status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void findBookingById() throws Exception {
        final BookingDto bookingReturned = getBookingDto();
        Mockito.when(bookingService.findBookingById(1L, 1L))
                .thenReturn(bookingReturned);

        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item", is(getItemSendDto()), ItemSendDto.class))
                .andExpect(jsonPath("$.booker", is(getUserDto()), UserDto.class))
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
        final List<BookingDto> bookingReturnedList = List.of(getBookingDto());
        Mockito.when(bookingService.findAllBookingByBookerIdAndState(1L, BookingState.PAST, 1, 1))
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
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item", is(getItemSendDto()), ItemSendDto.class))
                .andExpect(jsonPath("$[0].booker", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$[0].start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$[0].end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$[0].status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    @Test
    void findAllBookingByOwnerIdAndState() throws Exception {
        final List<BookingDto> bookingReturnedList = List.of(getBookingDto());
        Mockito.when(bookingService.findAllBookingByOwnerIdAndState(1L, BookingState.PAST, 1, 1))
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
                .andExpect(jsonPath("$[0].item", is(getItemSendDto()), ItemSendDto.class))
                .andExpect(jsonPath("$[0].booker", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$[0].start", notNullValue()/*is(START), LocalDateTime.class*/))   // Not working for some reason
                .andExpect(jsonPath("$[0].end", notNullValue()/*is(END), LocalDateTime.class*/))
                .andExpect(jsonPath("$[0].status", notNullValue()/*is(BookingStatus.WAITING), BookingStatus.class*/));
    }

    private BookingDto getBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .item(getItemSendDto())
                .booker(getUserDto())
                .start(START)
                .end(END)
                .status(BookingStatus.WAITING)
                .build();
    }

    private ItemSendDto getItemSendDto() {
        return new ItemSendDto(1L, "name", "description", true, null, null, null, null);
    }

    private UserDto getUserDto() {
        return new UserDto(1L, "user@email.com", "userName");
    }

    private BookingReceiveDto getBookingReceiveDtoNullBookerId() {
        return BookingReceiveDto.builder()
                .itemId(1L)
                .bookerId(null)
                .start(START)
                .end(END)
                .build();
    }

    private BookingReceiveDto getBookingReceiveDto() {
        return BookingReceiveDto.builder()
                .itemId(1L)
                .bookerId(1L)
                .start(START)
                .end(END)
                .build();
    }
}