package ru.practicum.shareit.booking.state;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookingStateEnumConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String s) {
        return BookingState.fromString(s);
    }
}
