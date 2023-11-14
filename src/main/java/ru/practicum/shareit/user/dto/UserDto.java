package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email(message = "Invalid user's email")
    @NotBlank(message = "Film email can't be null or empty")
    private String email;

    @NotBlank(message = "Invalid user's name. It should not be null or empty")
    private String name;
}
