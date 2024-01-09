package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService itemRequestService;

    private static final LocalDateTime CREATED = LocalDateTime.now();

    @Test
    void create() throws Exception {
        final ItemRequestRequestingDto itemRequestToSave = getItemRequestReceiveDto();
        final ItemRequestInfoDto itemRequestSaved = getItemRequestSendDto();
        Mockito.when(itemRequestService.create(Mockito.any(ItemRequestRequestingDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestSaved);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestToSave))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", nullValue()));
    }

    @Test
    void getById() throws Exception {
        final ItemRequestInfoDto ItemRequestSaved = getItemRequestSendDto();
        Mockito.when(itemRequestService.getById(1L, 1L))
                .thenReturn(ItemRequestSaved);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", nullValue()));
    }

    @Test
    void getByAllByRequestingUserId() throws Exception {
        final List<ItemRequestInfoDto> ItemRequestSavedList = List.of(getItemRequestSendDto());
        Mockito.when(itemRequestService.getByAllByRequestingUserId(1L))
                .thenReturn(ItemRequestSavedList);

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", nullValue()));
    }

    @Test
    void getAllWithOffsetAndLimit() throws Exception {
        int limit = 1;
        int offset = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        final List<ItemRequestInfoDto> ItemRequestSavedList = List.of(getItemRequestSendDto());
        Mockito.when(itemRequestService.getAllWithOffsetAndLimit(1L, pageable))
                .thenReturn(ItemRequestSavedList);

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "1")
                        .param("from", "1")
                )
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", nullValue()));
    }

    private ItemRequestInfoDto getItemRequestSendDto() {
        return ItemRequestInfoDto.builder()
                .id(1L)
                .description("description")
                .created(CREATED)
                .items(null)
                .build();
    }

    private ItemRequestRequestingDto getItemRequestReceiveDto() {
        return new ItemRequestRequestingDto("description");
    }
}