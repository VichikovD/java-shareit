package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;
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
        ItemRequestRequestingDto requestReceiveDto1 = new ItemRequestRequestingDto("itemRequest1");
        ItemRequestRequestingDto requestReceiveDto2 = new ItemRequestRequestingDto("itemRequest2");
        ItemRequestInfoDto itemRequestInfoDto1 = itemRequestService.create(requestReceiveDto1, 1L);
        ItemRequestInfoDto itemRequestInfoDto2 = itemRequestService.create(requestReceiveDto2, 1L);
        UserDto owner = UserDto.builder()
                .name("userName")
                .email("user@email.com")
                .build();
        userService.create(owner);
        ItemRequestingDto itemRequestingDto = ItemRequestingDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(1L)
                .build();
        itemService.create(itemRequestingDto, 2L);
        itemService.create(itemRequestingDto, 2L);
        int limit = 1;
        int offset = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);

        List<ItemRequestInfoDto> itemRequestInfoDtoList = itemRequestService.getAllWithOffsetAndLimit(2L, pageable);

        assertThat(itemRequestInfoDtoList.size(), is(1));
        assertThat(itemRequestInfoDtoList.get(0).getId(), is(1L));
    }
}