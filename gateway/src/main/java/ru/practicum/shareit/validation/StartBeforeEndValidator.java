package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingRequestingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingRequestingDto> {
    @Override
    public boolean isValid(BookingRequestingDto bookingRequestingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingRequestingDto.getStart();
        LocalDateTime end = bookingRequestingDto.getEnd();

        return start != null && end != null && bookingRequestingDto.getStart().isBefore(bookingRequestingDto.getEnd());
    }
}