package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    HashMap<Long, User> users;
    Long idCounter = 0L;

    public UserDaoImpl() {
        this.users = new HashMap<>();
    }

    private Long getNewId() {
        return ++idCounter;
    }

    @Override
    public User create(User user) {
        Long userId = getNewId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public boolean isUniqueEmail(UserDto userDto) {
        boolean flag = true;
        String email = userDto.getEmail();
        for (User user : getAll()) {
            if (user.getEmail().equals(email) && !Objects.equals(user.getId(), userDto.getId())) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
