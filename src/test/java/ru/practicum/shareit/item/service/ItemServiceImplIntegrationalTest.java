/*
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
        ItemRequestingDto itemReceiveDto = ItemRequestingDto.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .requestId(null)
                .build();

        ItemInfoDto returnedItem1 = itemService.create(itemReceiveDto, 1L);
        ItemInfoDto returnedItem2 = itemService.create(itemReceiveDto, 1L);

        assertThat(returnedItem1.getId(), is(1L));
        assertThat(returnedItem2.getId(), is(2L));

        List<ItemInfoDto> itemList = itemService.search("name", 1, 1);
        assertThat(itemList.size(), is(1));
        assertThat(itemList.get(0).getId(), is(2L));
    }
}*/
