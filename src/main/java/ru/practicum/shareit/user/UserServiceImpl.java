package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    UserDao userDao;
    UserMapper userMapper;

    public UserServiceImpl(@Qualifier("userDaoImpl") UserDao userDao,
                           UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        String email = userDto.getEmail();
        if (!userDao.isUniqueEmail(userDto)) {
            throw new AlreadyExistsException("Email \"" + email + "\" already used");
        }

        User userToCreate = userMapper.createUserFromUserDto(userDto);
        User userCreated = userDao.create(userToCreate);
        return userMapper.createUserDtoFromUser(userCreated);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        String email = userDto.getEmail();

        User userToUpdate = userDao.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + userId));
        if (!userDao.isUniqueEmail(userDto)) {
            throw new AlreadyExistsException("Email \"" + email + "\" already used");
        }
        // validateService.validateUserDtoNotNullFields(userDto);

        userMapper.updateUserByUserDtoNotNullFields(userDto, userToUpdate);
        userDao.update(userToUpdate);
        return userMapper.createUserDtoFromUser(userToUpdate);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userDao.getAll();
        return userMapper.createUserDtoListFromUserList(userList);
    }

    @Override
    public void deleteById(Long id) {
        userDao.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + id));

        userDao.deleteById(id);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userDao.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + id));
        return userMapper.createUserDtoFromUser(user);
    }
}
