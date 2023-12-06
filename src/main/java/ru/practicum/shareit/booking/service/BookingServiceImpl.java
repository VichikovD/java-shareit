package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoReceive;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository,
                              BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDtoSend create(BookingDtoReceive bookingDtoReceive) {
        long bookerId = bookingDtoReceive.getBookerId();
        long itemId = bookingDtoReceive.getItemId();
        LocalDateTime start = bookingDtoReceive.getStart();
        LocalDateTime end = bookingDtoReceive.getEnd();

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id " + bookerId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item to be booked not found by id: " + itemId));
        if (!item.getIsAvailable()) {
            throw new NotAvailableException("Item to be booked is not available");
        }
        if (!start.isBefore(end)) {
            throw new ValidateException("Start time \"" + start + "\" should be before end time \"" + end);
        }

        long ownerId = item.getOwner().getId();
        if (ownerId == bookerId) {
            throw new OwnerBookingHisItemException("Booker with Id=" + bookerId + " can't book item with owner Id=" + ownerId);
        }

        Booking booking = BookingMapper.bookingDtoReceiveToBooking(bookingDtoReceive, booker, item, BookingStatus.WAITING);
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDtoSend(bookingToReturn);
    }

    @Override
    public BookingDtoSend respondToBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for owner with id: " + ownerId));
        /*
        Booking booking = bookingRepository.findById(bookingId);
        Item bookingItem = booking.getItem();
        long itemOwnerId = bookingItem.getOwner()
                .getId();
        long itemId = bookingItem.getId();
        if (itemOwnerId != userId) {
            throw new LockedException(String.format("User with id %d is not owner of item with id %d", userId, itemId));
        }
        */
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new LockedException("Status can't be changed. Status locked as \"APPROVED\"");
        }
        booking.setStatus(BookingStatus.getBookingStatusByBoolean(approved));

        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDtoSend(bookingToReturn);
    }

    @Override
    public BookingDtoSend findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdOrBookerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for user with id: " + userId));
        /*Item bookingItem = booking.getItem();
        long bookerId = booking.getBooker()
                .getId();
        long itemOwnerId = bookingItem.getOwner()
                .getId();
        long itemId = bookingItem.getId();
        if (itemOwnerId != userId && bookerId != userId) {
            throw new LockedException(String.format("User with id %d is not owner or booker of item with id %d", userId, itemId));
        }*/

        return BookingMapper.bookingToBookingDtoSend(booking);
    }

    @Override
    public List<BookingDtoSend> findAllBookingByBookerIdAndState(long bookerId, BookingState state) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id " + bookerId));

        switch (state) {
            case ALL:
                return findAllBookingByBookerId(bookerId);
            case PAST:
                return findAllPastBookingByBookerId(bookerId);
            case CURRENT:
                return findAllCurrentBookingByBookerId(bookerId);
            case FUTURE:
                return findAllFutureBookingByBookerId(bookerId);
            case WAITING:
            case REJECTED:
                return findAllBookingByBookerIdAndStatus(bookerId, BookingStatus.valueOf(state.toString()));
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoSend> findAllBookingByBookerId(long bookerId) {
        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllPastBookingByBookerId(long bookerId) {
        List<Booking> bookingList = bookingRepository.findAllPastBookingByBookerIdOrderByStartDesc(bookerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllCurrentBookingByBookerId(long bookerId) {
        List<Booking> bookingList = bookingRepository.findAllCurrentBookingByBookerIdOrderByStartDesc(bookerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllFutureBookingByBookerId(long bookerId) {
        List<Booking> bookingList = bookingRepository.findAllFutureBookingByBookerIdOrderByStartDesc(bookerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllBookingByBookerIdAndStatus(long bookerId, BookingStatus status) {
        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, status);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllBookingByOwnerIdAndState(long ownerId, BookingState state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner user not found by id " + ownerId));

        switch (state) {
            case ALL:
                return findAllBookingByOwnerId(ownerId);
            case PAST:
                return findAllPastBookingByOwnerId(ownerId);
            case CURRENT:
                return findAllCurrentBookingByOwnerId(ownerId);
            case FUTURE:
                return findAllFutureBookingByOwnerId(ownerId);
            case WAITING:
            case REJECTED:
                return findAllBookingByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state.toString()));
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoSend> findAllBookingByOwnerId(long ownerId) {
        List<Booking> bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllPastBookingByOwnerId(long ownerId) {
        List<Booking> bookingList = bookingRepository.findAllPastBookingByItemOwnerIdOrderByStartDesc(ownerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllCurrentBookingByOwnerId(long ownerId) {
        List<Booking> bookingList = bookingRepository.findAllCurrentBookingByItemOwnerIdOrderByStartDesc(ownerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllFutureBookingByOwnerId(long ownerId) {
        List<Booking> bookingList = bookingRepository.findAllFutureBookingByItemOwnerIdOrderByStartDesc(ownerId);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Override
    public List<BookingDtoSend> findAllBookingByOwnerIdAndStatus(long ownerId, BookingStatus status) {
        List<Booking> bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status);
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }
}
