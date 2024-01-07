package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST \"/users\" Body=" + userDto);
        UserDto userToReturn = userService.create(userDto);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id,
                          @RequestBody UserDto user) {
        log.info("PATCH \"/users/" + id + "\" Body=" + user);
        user.setId(id);
        UserDto userToReturn = userService.update(user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET \"/users\"");
        List<UserDto> userList = userService.getAll();
        log.debug(userList.toString());
        return userList;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("GET \"/users/" + id + "\"");
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.info("DELETE \"/users/" + id + "\"");
        userService.deleteById(id);
    }
}