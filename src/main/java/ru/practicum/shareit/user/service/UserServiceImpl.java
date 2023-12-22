package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User userToCreate = UserMapper.createUserFromUserDto(userDto);
        User userCreated = userRepository.save(userToCreate);
        return UserMapper.createUserDtoFromUser(userCreated);
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        String email = userDto.getEmail();

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));

        UserMapper.updateUserByUserDtoNotNullFields(userDto, userToUpdate);
        userRepository.save(userToUpdate);
        return UserMapper.createUserDtoFromUser(userToUpdate);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return UserMapper.createUserDtoListFromUserList(userList);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + id));
        return UserMapper.createUserDtoFromUser(user);
    }
}
