package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(UserDto user);

    List<UserDto> getAll();

    void deleteById(Long id);

    UserDto getById(Long id);
}
