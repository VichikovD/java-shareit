package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.validation.EmailIfNotNull;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email(groups = OnCreate.class, message = "Invalid user's email")
    @EmailIfNotNull(groups = OnUpdate.class)
    @NotBlank(groups = OnCreate.class, message = "Film email can't be null or empty")
    private String email;

    @NotBlank(groups = OnCreate.class, message = "Invalid user's name. It should not be null or empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class)
    private String name;
}
