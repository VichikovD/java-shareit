package ru.practicum.shareit.booking.state;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.UnsupportedStateException;

@Component
public class BookingStateEnumConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String s) {
        // Подобный подход не ловит исключения. Как задать текст ошибки в конвертере иначе?
        try {
            return BookingState.valueOf(s);
        } catch (MethodArgumentTypeMismatchException e) {
            throw new UnsupportedStateException("Unknown state: " + e.getValue(), e);
        }
    }
}
