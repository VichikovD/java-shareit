package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    HashMap<Long, User> usersById;
    HashMap<String, User> usersByEmail;
    Long idCounter = 0L;

    public UserDaoImpl() {
        this.usersById = new HashMap<>();
        this.usersByEmail = new HashMap<>();
    }

    private Long getNewId() {
        return ++idCounter;
    }

    @Override
    public User create(User user) {
        Long userId = getNewId();
        user.setId(userId);
        usersById.put(userId, user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void update(User user) {
        long id = user.getId();
        String oldEmail = usersById.get(id)
                .getEmail();

        usersByEmail.remove(oldEmail);
        usersByEmail.put(user.getEmail(), user);
        usersById.put(id, user);
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = usersById.get(id);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                    User.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build());
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public void deleteById(Long id) {
        String oldEmail = usersById.get(id).getEmail();
        usersById.remove(id);
        usersByEmail.remove(oldEmail);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        User user = usersByEmail.get(email);
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                    User.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build());
        }
    }
}
