package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        log.info("POST \"/users\" Body=" + userDto);
        ResponseEntity<Object> userToReturn = userClient.create(userDto);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                                         @RequestBody @Validated(OnUpdate.class) UserDto user) {
        log.info("PATCH \"/users/" + id + "\" Body=" + user);
        ResponseEntity<Object> userToReturn = userClient.update(id, user);
        log.debug(userToReturn.toString());
        return userToReturn;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET \"/users\"");
        ResponseEntity<Object> userList = userClient.getAll();
        log.debug(userList.toString());
        return userList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("GET \"/users/" + id + "\"");
        ResponseEntity<Object> user = userClient.getById(id);
        log.debug(user.toString());
        return user;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        log.info("DELETE \"/users/" + id + "\"");
        return userClient.deleteById(id);
    }
}