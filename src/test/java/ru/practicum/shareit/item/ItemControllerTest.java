package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    private static final LocalDateTime CREATED = LocalDateTime.now();

    @Test
    void create() throws Exception {
        ItemReceiveDto itemToSave = getItemReceiveDtoNullId();
        ItemSendDto itemSendDto = getItemSendDto();
        Mockito.when(itemService.create(itemToSave, 1L))
                .thenReturn(itemSendDto);

        mvc.perform(post("/items").content(mapper.writeValueAsString(itemToSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", nullValue()));
    }

    @Test
    void update() throws Exception {
        ItemReceiveDto itemReceiveToSave = getItemReceiveDto();
        ItemSendDto itemSendDto = getItemSendDto();
        Mockito.when(itemService.update(itemReceiveToSave, 1L))
                .thenReturn(itemSendDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemReceiveToSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", nullValue()));
    }

    @Test
    void getByOwnerId() throws Exception {
        ItemReceiveDto itemReceiveToSave = getItemReceiveDto();
        List<ItemSendDto> itemSendDtoList = List.of(getItemSendDto());
        Mockito.when(itemService.getByOwnerId(1L, 1, 1))
                .thenReturn(itemSendDtoList);

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].requestId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$[0].nextBooking", nullValue()))
                .andExpect(jsonPath("$[0].comments", nullValue()));
    }

    @Test
    void getByItemId() throws Exception {
        ItemSendDto itemSendDto = getItemSendDto();
        Mockito.when(itemService.getByItemId(1L, 1L))
                .thenReturn(itemSendDto);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", nullValue()));
    }

    @Test
    void search() throws Exception {
        List<ItemSendDto> itemSendDtoList = List.of(getItemSendDto());
        Mockito.when(itemService.search("item", 1, 1))
                .thenReturn(itemSendDtoList);

        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("itemDescription")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].requestId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$[0].nextBooking", nullValue()))
                .andExpect(jsonPath("$[0].comments", nullValue()));
    }

    @Test
    void deleteByUserIdAndItemId() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentToSave = getCommentDtoNullId();
        CommentDto commentSaved = getCommentDto();
        Mockito.when(itemService.createComment(commentToSave, 1L, 1L))
                .thenReturn(commentSaved);

        mvc.perform(post("/items/1/comment").content(mapper.writeValueAsString(commentToSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is("text")))
                .andExpect(jsonPath("$.authorName", is("authorName")))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    private CommentDto getCommentDtoNullId() {
        return CommentDto.builder()
                .id(null)
                .text("text")
                .authorName("authorName")
                .created(CREATED)
                .itemId(1L)
                .build();
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(CREATED)
                .itemId(1L)
                .build();
    }

    private Item getItem(User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(1L)
                .owner(owner)
                .name("name")
                .description("description")
                .isAvailable(true)
                .itemRequest(itemRequest)
                .build();
    }

    private ItemSendDto getItemSendDto() {
        return ItemSendDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
    }

    private ItemReceiveDto getItemReceiveDtoNullId() {
        return ItemReceiveDto.builder()
                .id(null)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();
    }

    private ItemReceiveDto getItemReceiveDto() {
        return ItemReceiveDto.builder()
                .id(1L)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .name("userName")
                .email("user@email")
                .build();
    }

    private User getUserNullId() {
        return User.builder()
                .id(null)
                .name("userName")
                .email("user@email")
                .build();
    }
}