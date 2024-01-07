package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private static final LocalDateTime CREATED = LocalDateTime.now();

    @Test
    void create() {
        User requestingUser = getUser();
        ItemRequest itemRequestToSave = getItemRequestIdNull(requestingUser);
        ItemRequest itemRequestToReturn = getItemRequest(requestingUser);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(requestingUser));
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class)))  // any due to field "created" initialized in mapper
                .thenReturn(itemRequestToReturn);

        ItemRequestInfoDto actualRequestDto = itemRequestService.create(new ItemRequestRequestingDto("description"), 1L);

        assertThat(actualRequestDto.getId(), Matchers.is(1L));
        assertThat(actualRequestDto.getDescription(), Matchers.is("description"));
        assertThat(actualRequestDto.getCreated(), Matchers.greaterThan(CREATED.minusSeconds(10)));
        assertThat(actualRequestDto.getCreated(), Matchers.lessThan(CREATED.plusSeconds(10)));
        assertThat(actualRequestDto.getItems().size(), Matchers.is(0));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(any(ItemRequest.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);

    }

    @Test
    void create_whenUserNotFound_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(new ItemRequestRequestingDto("description"), 1L));
        assertThat(exception.getMessage(), Matchers.is("User not found by id: 1"));
    }

    @Test
    void getById() {
        User requestingUser = getUser();
        User respondingUser = getRespondingUser();
        ItemRequest itemRequestToReturn = getItemRequest(requestingUser);
        Item responseItem = getItem(respondingUser, itemRequestToReturn);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(requestingUser));
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequestToReturn));
        Mockito.when(itemRepository.findAllByItemRequestId(1L))
                .thenReturn(List.of(responseItem));

        ItemRequestInfoDto actualRequestDto = itemRequestService.getById(1L, 1L);
        ItemInfoDto response = actualRequestDto.getItems().get(0);

        assertThat(actualRequestDto.getId(), Matchers.is(1L));
        assertThat(actualRequestDto.getDescription(), Matchers.is("description"));
        assertThat(actualRequestDto.getCreated(), Matchers.greaterThan(CREATED.minusSeconds(10)));
        assertThat(actualRequestDto.getCreated(), Matchers.lessThan(CREATED.plusSeconds(10)));
        assertThat(actualRequestDto.getItems().size(), Matchers.is(1));
        assertThat(response.getId(), Matchers.is(1L));
        assertThat(response.getRequestId(), Matchers.is(1L));
        assertThat(response.getName(), Matchers.is("name"));
        assertThat(response.getDescription(), Matchers.is("description"));
        assertThat(response.getAvailable(), Matchers.is(true));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByItemRequestId(1L);
        /*itemRepository.findAllByItemRequestId*/
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository);
    }

    @Test
    void getByAllByRequestingUserId() {
    }

    @Test
    void getAllWithOffsetAndLimit() {
    }

    private Item getItem(User user, ItemRequest itemRequest) {
        return Item.builder()
                .id(1L)
                .owner(user)
                .name("name")
                .description("description")
                .isAvailable(true)
                .itemRequest(itemRequest)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("user@email.com")
                .name("name")
                .build();
    }

    private User getRespondingUser() {
        return User.builder()
                .id(3L)
                .email("3@email.com")
                .name("3name")
                .build();
    }

    private ItemRequest getItemRequestIdNull(User requestingUser) {
        return ItemRequest.builder()
                .id(null)
                .requestingUser(requestingUser)
                .description("description")
                .created(CREATED)
                .build();
    }

    private ItemRequest getItemRequest(User requestingUser) {
        return ItemRequest.builder()
                .id(1L)
                .requestingUser(requestingUser)
                .description("description")
                .created(CREATED)
                .build();
    }
}