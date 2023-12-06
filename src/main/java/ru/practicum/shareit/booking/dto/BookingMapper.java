package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {
    public static Booking bookingDtoReceiveToBooking(BookingDtoReceive bookingDtoReceive, User booker, Item item,
                                                     BookingStatus bookingStatus) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(Timestamp.valueOf(bookingDtoReceive.getStart()))
                .end(Timestamp.valueOf(bookingDtoReceive.getEnd()))
                .status(bookingStatus)
                .build();
    }

    public static BookingDtoItem bookingToBookingDtoItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDtoItem.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart().toLocalDateTime())
                .end(booking.getEnd().toLocalDateTime())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoSend bookingToBookingDtoSend(Booking booking) {
        return BookingDtoSend.builder()
                .id(booking.getId())
                .item(ItemMapper.createItemDtoFromItem(booking.getItem()))
                .booker(UserMapper.createUserDtoFromUser(booking.getBooker()))
                .start(booking.getStart().toLocalDateTime())
                .end(booking.getEnd().toLocalDateTime())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDtoSend> bookingListToBookingDtoSendList(List<Booking> bookingList) {
        List<BookingDtoSend> bookingDtoSendList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoSendList.add(bookingToBookingDtoSend(booking));
        }
        return bookingDtoSendList;
    }
}
