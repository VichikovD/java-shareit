package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Invalid user's email")
    @NotBlank(groups = OnCreate.class, message = "Invalid user's email. It should not be null or empty")
    private String email;

    @NotBlank(groups = OnCreate.class, message = "Invalid user's name. It should not be null or empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class)
    private String name;
}
