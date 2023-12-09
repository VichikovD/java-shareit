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
    public static Booking bookingDtoReceiveToBooking(BookingReceiveDto bookingReceiveDto, User booker, Item item,
                                                     BookingStatus bookingStatus) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingReceiveDto.getStart())
                .end(bookingReceiveDto.getEnd())
                .status(bookingStatus)
                .build();
    }

    public static BookingDto bookingToBookingDtoSend(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(ItemMapper.itemReceiveDtoFromItem(booking.getItem()))
                .booker(UserMapper.createUserDtoFromUser(booking.getBooker()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> bookingListToBookingDtoSendList(List<Booking> bookingList) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(bookingToBookingDtoSend(booking));
        }
        return bookingDtoList;
    }
}
