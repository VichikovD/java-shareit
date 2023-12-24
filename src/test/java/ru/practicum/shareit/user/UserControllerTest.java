package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @Test
    void create() throws Exception {
        UserDto userToSave = getUserDtoNullId();
        UserDto userSaved = getUserDto();
        Mockito.when(userService.create(userToSave))
                .thenReturn(userSaved);

        mvc.perform(post("/users").content(mapper.writeValueAsString(userToSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.email", is("user@email")))
                .andExpect(jsonPath("$.name", is("userName")));
    }

    @Test
    void update() throws Exception {
        UserDto user = getUserDto();
        Mockito.when(userService.update(user))
                .thenReturn(user);
        mvc.perform(patch("/users/1").content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.email", is("user@email")))
                .andExpect(jsonPath("$.name", is("userName")));
    }

    @Test
    void getAll() throws Exception {
        List<UserDto> userList = List.of(getUserDto());
        Mockito.when(userService.getAll())
                .thenReturn(userList);

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].email", is("user@email")))
                .andExpect(jsonPath("$[0].name", is("userName")));
    }

    @Test
    void getById() throws Exception {
        UserDto user = getUserDto();
        Mockito.when(userService.getById(1L))
                .thenReturn(user);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.email", is("user@email")))
                .andExpect(jsonPath("$.name", is("userName")));
    }

    @Test
    void deleteById() throws Exception {
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("userName")
                .email("user@email")
                .build();
    }

    private UserDto getUserDtoNullId() {
        return UserDto.builder()
                .id(null)
                .name("userName")
                .email("user@email")
                .build();
    }
}