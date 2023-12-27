package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReceiveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END = LocalDateTime.now().plusDays(2);
    private static final long BOOKER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final long OWNER_ID = 2L;


    @Test
    void create_whenUserNotFoundByBookerId_thenThrowsNotFoundException() {
        Mockito.when(userRepository.findById(BOOKER_ID))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(getBookingReceiveDto()));
        assertThat(exception.getMessage(), Matchers.is("Booking user not found by id: 1"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(BOOKER_ID);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void create_whenItemNotFoundByItemId_thenThrowsNotFoundException() {
        User booker = getBooker();
        Mockito.when(userRepository.findById(BOOKER_ID))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(ITEM_ID))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(getBookingReceiveDto()));
        assertThat(exception.getMessage(), Matchers.is("Item to be booked not found by id: 1"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(BOOKER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(ITEM_ID);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void create_whenItemIsAvailableFalse_thenThrowsValidateException() {
        User booker = getBooker();
        Item item = getItem(getOwner(), null);
        item.setIsAvailable(false);
        Mockito.when(userRepository.findById(BOOKER_ID))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(ITEM_ID))
                .thenReturn(Optional.of(item));

        ValidateException exception = assertThrows(ValidateException.class,
                () -> bookingService.create(getBookingReceiveDto()));
        assertThat(exception.getMessage(), Matchers.is("Item to be booked is not available"));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(BOOKER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(ITEM_ID);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void create_whenIntersectionsFoundByStartAndEnd_thenThrowsValidateException() {
        User booker = getBooker();
        Item item = getItem(getOwner(), null);
        Mockito.when(userRepository.findById(BOOKER_ID))
                .thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(ITEM_ID))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countIntersectionInTime(START, END, ITEM_ID))
                .thenReturn(1);

        ValidateException exception = assertThrows(ValidateException.class,
                () -> bookingService.create(getBookingReceiveDto()));
        assertThat(exception.getMessage(), Matchers.is("Item to be booked is already booked in between date " + START + " and " + END));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(BOOKER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(ITEM_ID);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .countIntersectionInTime(START, END, ITEM_ID);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void create_whenItemOwnerIdEqualsBookerId_thenThrowsNotFoundException() {
        User bookerOwner = getOwner();
        BookingReceiveDto bookingReceiveDto = getBookingReceiveDto();
        bookingReceiveDto.setBookerId(OWNER_ID);   // now booker is owner of item
        Item item = getItem(bookerOwner, null);    // now booker is owner of item
        Mockito.when(userRepository.findById(OWNER_ID))    // find user by ownerId
                .thenReturn(Optional.of(bookerOwner));
        Mockito.when(itemRepository.findById(ITEM_ID))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countIntersectionInTime(START, END, ITEM_ID))
                .thenReturn(0);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingReceiveDto));
        assertThat(exception.getMessage(), Matchers.is("Booker with Id=" + OWNER_ID + " can't book item with owner Id=" + OWNER_ID));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(OWNER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(ITEM_ID);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .countIntersectionInTime(START, END, ITEM_ID);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void create() {
        User booker = getBooker();
        Mockito.when(userRepository.findById(BOOKER_ID))
                .thenReturn(Optional.of(booker));
        Item item = getItem(getOwner(), null);
        Mockito.when(itemRepository.findById(ITEM_ID))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.countIntersectionInTime(START, END, ITEM_ID))
                .thenReturn(0);
        Booking bookingToSave = getBookingIdNull(item, booker, BookingStatus.WAITING);
        Booking bookingSaved = getBooking(item, booker, BookingStatus.WAITING);
        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingSaved);

        BookingDto actualBookingSendDto = bookingService.create(getBookingReceiveDto());

        assertThat(actualBookingSendDto.getId(), Matchers.is(1L));
        assertThat(actualBookingSendDto.getItem().getId(), Matchers.is(1L));
        assertThat(actualBookingSendDto.getBooker().getId(), Matchers.is(1L));
        assertThat(actualBookingSendDto.getStart(), Matchers.is(START));
        assertThat(actualBookingSendDto.getEnd(), Matchers.is(END));
        assertThat(actualBookingSendDto.getStatus(), Matchers.is(BookingStatus.WAITING));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(BOOKER_ID);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(ITEM_ID);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .countIntersectionInTime(START, END, ITEM_ID);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(bookingToSave);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void respondToBooking() {
        User owner = getOwner();
        Item item = getItem(owner, null);
        Booking booking = getBooking(item, owner, BookingStatus.WAITING);
        long bookingId = booking.getId();
        ;
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(1L, OWNER_ID))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.countIntersectionInTime(START, END, item.getId()))
                .thenReturn(0);
        Booking bookingToSave = getBooking(item, owner, BookingStatus.APPROVED);
        Mockito.when(bookingRepository.save(bookingToSave))
                .thenReturn(bookingToSave);

        BookingDto actualBookingDto = bookingService.respondToBooking(OWNER_ID, 1L, true);

        assertThat(actualBookingDto.getId(), Matchers.is(1L));
        assertThat(actualBookingDto.getItem().getId(), Matchers.is(1L));
        assertThat(actualBookingDto.getBooker().getId(), Matchers.is(2L));
        assertThat(actualBookingDto.getStart(), Matchers.is(START));
        assertThat(actualBookingDto.getEnd(), Matchers.is(END));
        assertThat(actualBookingDto.getStatus(), Matchers.is(BookingStatus.APPROVED));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByIdAndItemOwnerId(bookingId, OWNER_ID);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .countIntersectionInTime(START, END, bookingId);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(bookingToSave);
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void findBookingById_whenBookingNotFound_thenThrowsNotFoundException() {
        Mockito.when(bookingRepository.findByIdAndOwnerIdOrBookerId(1L, 2L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(2L, 1L));
        assertThat(exception.getMessage(), Matchers.is("Booking with id 1 not found for user with id: 2"));
    }

    @Test
    void findBookingById() {
        User booker = getBooker();
        User owner = getOwner();
        Item item = getItem(owner, null);
        Booking booking = getBooking(item, booker, BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findByIdAndOwnerIdOrBookerId(1L, 2L))
                .thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = bookingService.findBookingById(owner.getId(), booking.getId());

        assertThat(actualBookingDto.getId(), Matchers.is(1L));
        assertThat(actualBookingDto.getItem().getId(), Matchers.is(1L));
        assertThat(actualBookingDto.getBooker().getId(), Matchers.is(1L));
        assertThat(actualBookingDto.getStart(), Matchers.is(START));
        assertThat(actualBookingDto.getEnd(), Matchers.is(END));
        assertThat(actualBookingDto.getStatus(), Matchers.is(BookingStatus.APPROVED));
    }

    @Test
    void findAllBookingByBookerIdAndState_whenUserNotFound_thenThrowsNotFoundException() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingByBookerIdAndState(1L, BookingState.ALL, pageable));
        assertThat(exception.getMessage(), Matchers.is("Booking user not found by id: 1"));
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStateAll_thenCaseAllOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));

        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.ALL, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByBookerId(1L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStatePast_thenCasePastOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));


        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.PAST, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllPastBookingByBookerId(1L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStateCurrent_thenCaseCurrentOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));

        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.CURRENT, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllCurrentBookingByBookerId(1L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStateFuture_thenCaseFutureOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));

        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.FUTURE, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllFutureBookingByBookerId(1L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStateWaiting_thenCaseWaitingOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));

        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.WAITING, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByBookerIdAndStatus(1L, BookingStatus.WAITING, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByBookerIdAndState_whenStateRejected_thenCaseRejectedOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(getBooker()));

        bookingService.findAllBookingByBookerIdAndState(1L, BookingState.REJECTED, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByBookerIdAndStatus(1L, BookingStatus.REJECTED, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenUserNotFound_thenThrowsNotFoundException() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.ALL, pageable));
        assertThat(exception.getMessage(), Matchers.is("Owner user not found by id: 2"));
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStateAll_thenCaseAllOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.ALL, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByItemOwnerId(2L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStatePast_thenCasePastOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.PAST, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllPastBookingByItemOwnerId(2L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStateCurrent_thenCaseCurrentOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.CURRENT, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllCurrentBookingByItemOwnerId(2L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStateFuture_thenCaseFutureOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.FUTURE, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllFutureBookingByItemOwnerId(2L, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStateWaiting_thenCaseWaitingOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.WAITING, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByItemOwnerIdAndStatus(2L, BookingStatus.WAITING, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    @Test
    void findAllBookingByOwnerIdAndState_whenStateRejected_thenCaseRejectedOnlyInvocated() {
        int limit = 1;
        int offset = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(getOwner()));

        bookingService.findAllBookingByOwnerIdAndState(2L, BookingState.REJECTED, pageable);

        Mockito.verify(bookingRepository, Mockito.times(1))  // testing only repository method invocation
                .findAllByItemOwnerIdAndStatus(2L, BookingStatus.REJECTED, pageable); // due to the only happening next is DTO mapping
        Mockito.verifyNoMoreInteractions(bookingRepository); // (which can be tested separately)
    }

    private Booking getBookingIdNull(Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .id(null)
                .item(item)
                .booker(booker)
                .start(START)
                .end(END)
                .status(status)
                .build();
    }

    private Booking getBooking(Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(START)
                .end(END)
                .status(status)
                .build();
    }

    private BookingReceiveDto getBookingReceiveDto() {
        return BookingReceiveDto.builder()
                .bookerId(1L)
                .itemId(1L)
                .start(START)
                .end(END)
                .build();
    }

    private User getBooker() {
        return User.builder()
                .id(1L)
                .email("user@email.com")
                .name("name")
                .build();
    }

    private User getOwner() {
        return User.builder()
                .id(OWNER_ID)
                .email("owner@email.com")
                .name("ownerName")
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