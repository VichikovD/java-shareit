package ru.practicum.shareit.user.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    // MockedStatic<UserMapper> userMapperMockedStatic = Mockito.mockStatic(UserMapper.class);

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void create() {
        UserDto userDtoNullId = getUserDtoNullId();
        User user = getUser();
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto actualUserDto = userService.create(userDtoNullId);

        assertThat(actualUserDto.getId(), Matchers.is(1L));
        assertThat(actualUserDto.getName(), Matchers.is("name"));
        assertThat(actualUserDto.getEmail(), Matchers.is("email@email.com"));
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void update_whenFindByIdReturnsNull_thenThrowException() {
        UserDto userDto = getUserDto();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());  /*Throw(new NotFoundException("User not found by Id 1"));*/

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(userDto));
        assertThat(exception.getMessage(), Matchers.is("User not found by id: 1"));

    }

    @Test
    void update() {
        UserDto userDtoReceived = UserDto.builder()
                .id(1L)
                .email("new@email.ru")
                .name(null)
                .build();
        User userBeforeUpdate = getUser();
        Mockito.when(userRepository.findById(userDtoReceived.getId()))
                .thenReturn(Optional.of(userBeforeUpdate));
        User userAfterUpdate = User.builder()
                .id(1L)
                .name("name")
                .email("new@email.ru")
                .build();

        UserDto actualUserDto = userService.update(userDtoReceived);

        assertThat(actualUserDto.getId(), Matchers.is(1L));
        assertThat(actualUserDto.getName(), Matchers.is("name"));
        assertThat(actualUserDto.getEmail(), Matchers.is("new@email.ru"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
        // equals&hashCode by id only (not fully correct verifying)
        Mockito.verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(getUser()));

        List<UserDto> actualUserDtoList = userService.getAll();
        UserDto actualUser1 = actualUserDtoList.get(0);

        assertThat(actualUserDtoList.size(), Matchers.is(1));
        assertThat(actualUser1.getId(), Matchers.is(1L));
        assertThat(actualUser1.getName(), Matchers.is("name"));
        assertThat(actualUser1.getEmail(), Matchers.is("email@email.com"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

    // кажется, методы с 1 строчкой, которую все равно замокаешь нет смысла тестировать
    @Test
    void deleteById() {
        userService.deleteById(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void getById() {
        User user = getUser();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getById(1L);

        assertThat(actualUserDto.getId(), Matchers.is(1L));
        assertThat(actualUserDto.getEmail(), Matchers.is("email@email.com"));
        assertThat(actualUserDto.getName(), Matchers.is("name"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
    }


    private User getUserNullId() {
        return User.builder()
                .id(null)
                .email("email@email.com")
                .name("name")
                .build();
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
    }

    private UserDto getUserDtoNullId() {
        return UserDto.builder()
                .id(null)
                .email("email@email.com")
                .name("name")
                .build();
    }
}