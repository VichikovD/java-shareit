package ru.practicum.shareit.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.regex.Pattern;

@Component
public class ValidateService {
    public void validateId(Long id) {
        if (id == null) {
            throw new InvalidIdException("Id should not be empty");
        }
    }

    public void validateUserDtoNotNullFields(UserDto userDto) {
        String emailRegex = "^(.+)@(\\S+)$";
        Pattern pattern = Pattern.compile(emailRegex);

        String name = userDto.getName();
        if (name != null && name.isEmpty()) {
            throw new ValidateException("Name should not be empty");
        }

        String email = userDto.getEmail();
        if (email != null && (email.isEmpty() || !pattern.matcher(email).matches())) {
            throw new ValidateException("Email \"" + email + "\" is invalid");
        }
    }
}
