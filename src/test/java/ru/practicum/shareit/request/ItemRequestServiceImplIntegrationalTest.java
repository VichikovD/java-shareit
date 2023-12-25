package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestReceiveDto;
import ru.practicum.shareit.request.dto.ItemRequestSendDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationalTest {
    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ItemServiceImpl itemService;

    // For some reason @DirtiesContext doesn't clean context and user created with id 2
    @Test
    void getAllWithOffsetAndLimit_when2RequestsBut1OffsetAnd1Limit_thenReturn1Request() {
        UserDto requestingUser = UserDto.builder()
                .name("requestingName")
                .email("requesting@email.com")
                .build();
        UserDto requester = userService.create(requestingUser);
        ItemRequestReceiveDto requestReceiveDto1 = new ItemRequestReceiveDto("itemRequest1");
        ItemRequestReceiveDto requestReceiveDto2 = new ItemRequestReceiveDto("itemRequest2");
        ItemRequestSendDto itemRequestSendDto1 = itemRequestService.create(requestReceiveDto1, 2L);
        ItemRequestSendDto itemRequestSendDto2 = itemRequestService.create(requestReceiveDto2, 2L);
        UserDto owner = UserDto.builder()
                .name("userName")
                .email("user@email.com")
                .build();
        userService.create(owner);
        ItemReceiveDto itemReceiveDto = ItemReceiveDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();
        itemService.create(itemReceiveDto, 3L);
        itemService.create(itemReceiveDto, 3L);

        List<ItemRequestSendDto> itemRequestSendDtoList = itemRequestService.getAllWithOffsetAndLimit(3L, 1L, 1L);

        assertThat(itemRequestSendDtoList.size(), is(1));
        assertThat(itemRequestSendDtoList.get(0).getId(), is(1L));
    }
}