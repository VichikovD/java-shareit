package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User userToCreate = UserMapper.createUserFromUserDto(userDto);
        User userCreated = userRepository.save(userToCreate);
        return UserMapper.createUserDtoFromUser(userCreated);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        String email = userDto.getEmail();

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + userId));

        Optional<User> optUserByEmail = userRepository.findByEmail(email);
        if (optUserByEmail.isPresent()) {
            User userByEmail = optUserByEmail.get();
            if (!userByEmail.getId().equals(userId)) {
                throw new AlreadyExistsException("Email \"" + email + "\" already used");
            }
        }

        UserMapper.updateUserByUserDtoNotNullFields(userDto, userToUpdate);
        userRepository.save(userToUpdate);
        return UserMapper.createUserDtoFromUser(userToUpdate);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return UserMapper.createUserDtoListFromUserList(userList);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + id));

        userRepository.deleteById(id);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + id));
        return UserMapper.createUserDtoFromUser(user);
    }
}
