package ru.practicum.shareit.item.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    private static final long OWNER_ID = 2L;
    private static final long ITEM_REQUEST_ID = 1L;
    private static final LocalDateTime CREATED = LocalDateTime.now();

    @Test
    void create_whenNotFoundUserById_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(OWNER_ID))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.create(getItemReceiveDtoNullRequestId(), OWNER_ID));
        assertThat(exception.getMessage(), Matchers.is("User not found by id: " + OWNER_ID));
    }

    @Test
    void create() {
        ItemReceiveDto itemReceiveDto = getItemReceiveDtoWithRequestId();
        User owner = getOwner();
        ItemRequest itemRequest = getItemRequest();
        Item itemNullId = getItemNullId(owner, itemRequest);
        Item itemWithId = getItem(owner, itemRequest);
        Mockito.when(userRepository.findById(OWNER_ID))
                .thenReturn(Optional.of(owner));
        Mockito.when(itemRequestRepository.findById(itemReceiveDto.getRequestId()))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(itemNullId))
                .thenReturn(itemWithId);

        ItemSendDto actualItemSendDto = itemService.create(itemReceiveDto, OWNER_ID);

        assertThat(actualItemSendDto.getId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getName(), Matchers.is("name"));
        assertThat(actualItemSendDto.getDescription(), Matchers.is("description"));
        assertThat(actualItemSendDto.getAvailable(), Matchers.is(true));
        assertThat(actualItemSendDto.getRequestId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getLastBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getNextBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getComments(), Matchers.nullValue());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(OWNER_ID);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(itemReceiveDto.getRequestId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemNullId);
    }

    @Test
    void update_whenNotFoundUserById_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(OWNER_ID))
                .thenReturn(Optional.empty());
        ItemReceiveDto itemReceiveDto = ItemReceiveDto.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .requestId(1L)
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemReceiveDto, OWNER_ID));
        assertThat(exception.getMessage(), Matchers.is("User not found by id: " + OWNER_ID));
    }

    @Test
    void update() {
        User owner = getOwner();
        ItemRequest itemRequest = getItemRequest();
        ItemReceiveDto itemReceiveDto = ItemReceiveDto.builder()
                .id(1L)
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .requestId(1L)
                .build();
        Item itemBeforeUpdate = getItem(owner, itemRequest);
        Item itemAfterUpdate = Item.builder()
                .id(1L)
                .owner(owner)
                .name("updatedName")
                .description("updatedDescription")
                .isAvailable(true)
                .itemRequest(itemRequest)
                .build();
        Mockito.when(userRepository.findById(OWNER_ID))
                .thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findByIdAndOwnerId(itemReceiveDto.getId(), OWNER_ID))
                .thenReturn(Optional.of(itemBeforeUpdate));
        Mockito.when(itemRepository.save(itemAfterUpdate))
                .thenReturn(itemAfterUpdate);

        ItemSendDto actualItemSendDto = itemService.update(itemReceiveDto, OWNER_ID);

        assertThat(actualItemSendDto.getId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getName(), Matchers.is("updatedName"));
        assertThat(actualItemSendDto.getDescription(), Matchers.is("updatedDescription"));
        assertThat(actualItemSendDto.getAvailable(), Matchers.is(true));
        assertThat(actualItemSendDto.getRequestId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getLastBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getNextBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getComments(), Matchers.nullValue());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(OWNER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByIdAndOwnerId(1L, 2L);
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemAfterUpdate);
    }

    @Test
    void getByItemId() {
        long itemId = 1L;
        User owner = getOwner();
        User commentAndBookingUser = getUser();
        Item item = getItem(owner, null);
        Comment comment = getComment(item, commentAndBookingUser);
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(itemId))
                .thenReturn(List.of(comment));
        ItemSendDto actualItemSendDto = itemService.getByItemId(itemId, OWNER_ID);
        CommentDto actualComment = actualItemSendDto.getComments().get(0);

        assertThat(actualItemSendDto.getId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getName(), Matchers.is("name"));
        assertThat(actualItemSendDto.getDescription(), Matchers.is("description"));
        assertThat(actualItemSendDto.getAvailable(), Matchers.is(true));
        assertThat(actualItemSendDto.getRequestId(), Matchers.nullValue());
        assertThat(actualItemSendDto.getLastBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getNextBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getComments().size(), Matchers.is(1));
        assertThat(actualComment.getItemId(), Matchers.is(1L));
        assertThat(actualComment.getId(), Matchers.is(1L));
        assertThat(actualComment.getText(), Matchers.is("text"));
        assertThat(actualComment.getAuthorName(), Matchers.is("name"));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemId);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(1L);
    }

    @Test
    void getByOwnerId() {
        long itemId = 1L;
        int offset = 0;
        int limit = 1;
        User owner = getOwner();
        Item item = getItem(owner, null);
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.by(Sort.Direction.ASC, "id"));
        Comment comment = getComment(item, getUser());
        Mockito.when(itemRepository.findAllByOwnerId(OWNER_ID, pageRequest))
                .thenReturn(List.of(item));
        Mockito.when(commentRepository.findAllCommentsInIdList(List.of(itemId)))
                .thenReturn(List.of(comment));

        List<ItemSendDto> actualItemSendDtoList = itemService.getByOwnerId(OWNER_ID, pageRequest);
        ItemSendDto actualItemSendDto = actualItemSendDtoList.get(0);
        CommentDto actualComment = actualItemSendDto.getComments().get(0);
        ItemSendDto.BookingDtoItem actualLastBooking = actualItemSendDto.getLastBooking();
        ItemSendDto.BookingDtoItem actualNextBooking = actualItemSendDto.getNextBooking();

        assertThat(actualItemSendDto.getId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getName(), Matchers.is("name"));
        assertThat(actualItemSendDto.getDescription(), Matchers.is("description"));
        assertThat(actualItemSendDto.getAvailable(), Matchers.is(true));
        assertThat(actualItemSendDto.getRequestId(), Matchers.nullValue());
        assertThat(actualItemSendDto.getLastBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getNextBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getComments().size(), Matchers.is(1));
        assertThat(actualComment.getItemId(), Matchers.is(1L));
        assertThat(actualComment.getId(), Matchers.is(1L));
        assertThat(actualComment.getText(), Matchers.is("text"));
        assertThat(actualComment.getAuthorName(), Matchers.is("name"));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(OWNER_ID, pageRequest);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllCommentsInIdList(List.of(itemId));
    }

    @Test
    void search() {
        int offset = 0;
        int limit = 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Item item = getItem(getOwner(), null);
        Mockito.when(itemRepository.searchAvailableByNameOrDescription("name", pageable))
                .thenReturn(List.of(item));

        List<ItemSendDto> actualItemSendDtoList = itemService.search("NamE", pageable);
        ItemSendDto actualItemSendDto = actualItemSendDtoList.get(0);

        assertThat(actualItemSendDtoList.size(), Matchers.is(1));
        assertThat(actualItemSendDto.getId(), Matchers.is(1L));
        assertThat(actualItemSendDto.getName(), Matchers.is("name"));
        assertThat(actualItemSendDto.getDescription(), Matchers.is("description"));
        assertThat(actualItemSendDto.getAvailable(), Matchers.is(true));
        assertThat(actualItemSendDto.getRequestId(), Matchers.nullValue());
        assertThat(actualItemSendDto.getLastBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getNextBooking(), Matchers.nullValue());
        assertThat(actualItemSendDto.getComments(), Matchers.nullValue());
        Mockito.verify(itemRepository, Mockito.times(1))
                .searchAvailableByNameOrDescription("name", pageable);
    }

    @Test
    void search_whenTextIsBlank_thenReturnEmptyList() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<ItemSendDto> actualListTextEmpty = itemService.search("", pageable);
        List<ItemSendDto> actualListTextNull = itemService.search(null, pageable);

        assertThat(actualListTextEmpty.size(), Matchers.is(0));
        assertThat(actualListTextNull.size(), Matchers.is(0));
    }

    @Test
    void deleteByItemId() {
        itemService.deleteByItemId(1L, 2L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteByIdAndOwnerId(1L, 2L);
    }

    @Test
    void createComment_whenUserNotFound_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(getCommentDto(), 1L, 1L));
        assertThat(exception.getMessage(), Matchers.is("User not found by id: 1"));
    }

    @Test
    void createComment_whenItemNotFound_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getUser()));

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(getCommentDto(), 1L, 1L));
        assertThat(exception.getMessage(), Matchers.is("Item not found by id: 1"));
    }

    @Test
    void createComment_whenNotFoundBookingsInPast_thenThrowsNotAvailableException() {
        User commentator = getUser();
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(commentator));
        Item item = getItem(commentator, null);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countAllPastForItemByTime(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(0L);
        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> itemService.createComment(getCommentDto(), 1L, 1L));
        assertThat(exception.getMessage(),
                Matchers.is("User with id 1 can not comment item with id 1 due to 0 times booked this item in past"));
    }

    @Test
    void createComment() {
        User commentator = getUser();
        Mockito.when(bookingRepository.countAllPastForItemByTime(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(1L);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(commentator));
        Item item = getItem(commentator, null);
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Comment commentToSave = getComment(item, commentator);
        commentToSave.setId(null);
        Comment commentSaved = getComment(item, commentator);
        Mockito.when(commentRepository.save(commentToSave))
                .thenReturn(commentSaved);
        CommentDto commentToCreate = commentDtoFromComment(commentToSave);

        CommentDto actualCommentDto = itemService.createComment(commentToCreate, 1L, 1L);

        assertThat(actualCommentDto.getId(), Matchers.is(1L));
        assertThat(actualCommentDto.getText(), Matchers.is("text"));
        assertThat(actualCommentDto.getAuthorName(), Matchers.is("name"));
        assertThat(actualCommentDto.getCreated(), Matchers.notNullValue());
        assertThat(actualCommentDto.getItemId(), Matchers.is(1L));
    }

    private Booking getLastBooking(Item item, User booker) {
        return Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(CREATED.minusDays(2))
                .end(CREATED.minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
    }

    private Booking getNextBooking(Item item, User booker) {
        return Booking.builder()
                .id(2L)
                .item(item)
                .booker(booker)
                .start(CREATED.plusDays(1))
                .end(CREATED.plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
    }

    private Comment getComment(Item item, User author) {
        return Comment.builder()
                .id(1L)
                .text("text")
                .creationDate(Timestamp.valueOf(CREATED))
                .item(item)
                .author(author)
                .build();
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("text")
                .created(CREATED)
                .itemId(1L)
                .authorName("name")
                .build();
    }

    private CommentDto commentDtoFromComment(Comment dto) {
        return CommentDto.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(CREATED)
                .itemId(dto.getItem().getId())
                .authorName(dto.getAuthor().getName())
                .build();
    }

    private ItemReceiveDto getItemReceiveDtoNullRequestId() {
        return ItemReceiveDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();
    }

    private ItemReceiveDto getItemReceiveDtoWithRequestId() {
        return ItemReceiveDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("user@email.com")
                .name("name")
                .build();
    }

    private User getOwner() {
        return User.builder()
                .id(2L)
                .email("owner@email.com")
                .name("ownerName")
                .build();
    }

    private ItemRequest getItemRequest() {
        return ItemRequest.builder()
                .id(1L)
                .requestingUser(new User(2L, "requesting@user.com", "requestingUse"))
                .description("itemRequest")
                .created(CREATED)
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

    private Item getItemNullId(User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(null)
                .owner(owner)
                .name("name")
                .description("description")
                .isAvailable(true)
                .itemRequest(itemRequest)
                .build();
    }
}