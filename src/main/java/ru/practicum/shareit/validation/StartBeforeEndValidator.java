package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingReceiveDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingReceiveDto> {
    @Override
    public boolean isValid(BookingReceiveDto bookingReceiveDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingReceiveDto.getStart();
        LocalDateTime end = bookingReceiveDto.getEnd();

        return start != null && end != null && bookingReceiveDto.getStart().isBefore(bookingReceiveDto.getEnd());
    }
}