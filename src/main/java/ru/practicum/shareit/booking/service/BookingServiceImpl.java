package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingReceiveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.LockedException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    public static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDto create(BookingReceiveDto bookingReceiveDto) {
        long bookerId = bookingReceiveDto.getBookerId();
        long itemId = bookingReceiveDto.getItemId();
        LocalDateTime start = bookingReceiveDto.getStart();
        LocalDateTime end = bookingReceiveDto.getEnd();

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id " + bookerId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item to be booked not found by id: " + itemId));
        if (!item.getIsAvailable()) {
            throw new NotAvailableException("Item to be booked is not available");
        }
        long countIntersection = bookingRepository.countIntersectionInTime(start, end, itemId);
        if (countIntersection > 0) {
            throw new NotAvailableException("Item to be booked is already booked in between date " + start + " and " + end);
        }
        long ownerId = item.getOwner().getId();
        if (ownerId == bookerId) {
            throw new NotFoundException("Booker with Id=" + bookerId + " can't book item with owner Id=" + ownerId);
        }

        Booking booking = BookingMapper.bookingDtoReceiveToBooking(bookingReceiveDto, booker, item, BookingStatus.WAITING);
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDtoSend(bookingToReturn);
    }

    @Transactional
    @Override
    public BookingDto respondToBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for owner with id: " + ownerId));
        long itemId = booking.getItem().getId();
        LocalDateTime start = booking.getStart().toLocalDateTime();
        LocalDateTime end = booking.getEnd().toLocalDateTime();
        BookingStatus status = booking.getStatus();

        if (status.equals(BookingStatus.APPROVED)) {
            throw new LockedException("Status can't be changed. Status locked as \"" + status + "\"");
        }
        long countIntersection = bookingRepository.countIntersectionInTime(start, end, itemId);
        if (countIntersection > 0) {
            throw new NotAvailableException("Item to be booked is already booked in between date " + start + " and " + end);
        }

        booking.setStatus(BookingStatus.getBookingStatusByBoolean(approved));
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.bookingToBookingDtoSend(bookingToReturn);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
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

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingByBookerIdAndState(long bookerId, BookingState state) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id " + bookerId));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(bookerId, SORT_START_DESC);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByBookerId(bookerId, SORT_START_DESC);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByBookerId(bookerId, SORT_START_DESC);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByBookerId(bookerId, SORT_START_DESC);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, SORT_START_DESC);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, SORT_START_DESC);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingByOwnerIdAndState(long ownerId, BookingState state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner user not found by id " + ownerId));

        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(ownerId, SORT_START_DESC);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByItemOwnerId(ownerId, SORT_START_DESC);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByItemOwnerId(ownerId, SORT_START_DESC);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByItemOwnerId(ownerId, SORT_START_DESC);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_START_DESC);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_START_DESC);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return BookingMapper.bookingListToBookingDtoSendList(bookingList);
    }
}
