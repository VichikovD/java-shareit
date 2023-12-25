package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationalTest {
    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    UserServiceImpl userService;

    @Test
    void search() {
        UserDto owner = UserDto.builder()
                .name("userName")
                .email("user@email.com")
                .build();
        userService.create(owner);
        ItemReceiveDto itemReceiveDto = ItemReceiveDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(null)
                .build();

        ItemSendDto returnedItem1 = itemService.create(itemReceiveDto, 1L);
        ItemSendDto returnedItem2 = itemService.create(itemReceiveDto, 1L);

        assertThat(returnedItem1.getId(), is(1L));
        assertThat(returnedItem2.getId(), is(2L));

        List<ItemSendDto> itemList = itemService.search("name", 1, 1);
        assertThat(itemList.size(), is(1));
        assertThat(itemList.get(0).getId(), is(2L));
    }
}