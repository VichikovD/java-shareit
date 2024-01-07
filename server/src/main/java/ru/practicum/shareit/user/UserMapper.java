package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public static User createUserFromUserDto(UserDto userDto) {
        return User.builder()
                .id(null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto createUserDtoFromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserDto> createUserDtoListFromUserList(List<User> userList) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(createUserDtoFromUser(user));
        }
        return userDtoList;
    }

    public static void updateUserByUserDtoNotNullFields(UserDto userDto, User user) {
        String name = userDto.getName();
        if (name != null) {
            user.setName(name);
        }

        String email = userDto.getEmail();
        if (email != null) {
            user.setEmail(email);
        }
    }
}
