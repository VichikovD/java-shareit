package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    void update(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    void deleteById(Long id);

    Optional<User> getByEmail(String email);
}
