package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {
    public static Booking toModel(BookingCreateDto bookingCreateDto, User booker, Item item, BookingStatus bookingStatus) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .status(bookingStatus)
                .build();
    }

    public static BookingCreateDto toCreateDto(BookingRequestingDto bookingRequestingDto, long bookerId) {
        return BookingCreateDto.builder()
                .itemId(bookingRequestingDto.getItemId())
                .bookerId(bookerId)
                .start(bookingRequestingDto.getStart())
                .end(bookingRequestingDto.getEnd())
                .build();
    }

    public static BookingInfoDto toInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .item(ItemMapper.toItemInfoDto(booking.getItem()))
                .booker(UserMapper.createUserDtoFromUser(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingInfoDto> toBookingInfoDtoList(List<Booking> bookingList) {
        List<BookingInfoDto> bookingInfoDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingInfoDtoList.add(toInfoDto(booking));
        }
        return bookingInfoDtoList;
    }
}
