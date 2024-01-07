package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingInfoDto create(BookingCreateDto bookingCreateDto) {
        long bookerId = bookingCreateDto.getBookerId();
        long itemId = bookingCreateDto.getItemId();
        LocalDateTime start = bookingCreateDto.getStart();
        LocalDateTime end = bookingCreateDto.getEnd();

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id: " + bookerId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item to be booked not found by id: " + itemId));
        if (!item.getIsAvailable()) {
            throw new ValidateException("Item to be booked is not available");
        }
        // Проверка, что из всех approve бронирований ни 1 не пересекается с создаваемым, но т.к. создаются брони со статусом WAITING,
        // их можно создавать сколько угодно на одинаковые даты кем угодно, а уже владелец решит, кому достанется предмет.
        // Сделал так, чтобы бронировать предмет мог не первый успевший составить бронь, а каждый
        int countIntersection = bookingRepository.countIntersectionInTime(start, end, itemId);
        if (countIntersection > 0) {
            throw new ValidateException("Item to be booked is already booked in between date " + start + " and " + end);
        }
        long ownerId = item.getOwner().getId();
        if (ownerId == bookerId) {
            throw new NotFoundException("Booker with Id=" + bookerId + " can't book item with owner Id=" + ownerId);
        }

        Booking booking = BookingMapper.toModel(bookingCreateDto, booker, item, BookingStatus.WAITING);
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.toInfoDto(bookingToReturn);
    }

    @Transactional
    @Override
    public BookingInfoDto respondToBooking(long ownerId, long bookingId, boolean approved) {
        // метод ищет по одному конкретному booking id (Что уже не может вернуть список) и по owner id, чтобы убедиться,
        // что именно этот юзер владелец итема
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for owner with id: " + ownerId));
        long itemId = booking.getItem().getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingStatus status = booking.getStatus();

        if (!status.equals(BookingStatus.WAITING)) {
            throw new ValidateException("Status can't be changed. Status locked as \"" + status + "\"");
        }
        // А тут проверяем, что подтверждение брони не пересечется с уже approved бронью, ведь создавать брони мы можем
        // сколько угодно, пока на даты нет approved брони
        long countIntersection = bookingRepository.countIntersectionInTime(start, end, itemId);
        if (countIntersection > 0) {
            throw new ValidateException("Item to be booked is already booked in between date " + start + " and " + end);
        }

        booking.setStatus(BookingStatus.getBookingStatusByBoolean(approved));
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.toInfoDto(bookingToReturn);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingInfoDto findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdOrBookerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for user with id: " + userId));

        return BookingMapper.toInfoDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingInfoDto> findAllBookingByBookerIdAndState(long bookerId, BookingState state, Pageable pageable) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id: " + bookerId));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByBookerId(bookerId, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByBookerId(bookerId, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return BookingMapper.toBookingInfoDtoList(bookingList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingInfoDto> findAllBookingByOwnerIdAndState(long ownerId, BookingState state, Pageable pageable) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner user not found by id: " + ownerId));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(ownerId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByItemOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByItemOwnerId(ownerId, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByItemOwnerId(ownerId, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return BookingMapper.toBookingInfoDtoList(bookingList);
    }
}
