package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
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
public class BookingServiceImpl implements BookingService {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

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

        Booking booking = BookingMapper.bookingReceiveDtoToBooking(bookingReceiveDto, booker, item, BookingStatus.WAITING);
        Booking bookingToReturn = bookingRepository.save(booking);
        return BookingMapper.bookingToBookingSendDto(bookingToReturn);
    }

    @Transactional
    @Override
    public BookingDto respondToBooking(long ownerId, long bookingId, boolean approved) {
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
        return BookingMapper.bookingToBookingSendDto(bookingToReturn);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdOrBookerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found for user with id: " + userId));

        return BookingMapper.bookingToBookingSendDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingByBookerIdAndState(long bookerId, BookingState state, int limit, int offset) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Booking user not found by id: " + bookerId));
        PageRequest pageRequest = getPageRequest(Sort.Direction.DESC, "start", limit, offset);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(bookerId, pageRequest);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByBookerId(bookerId, pageRequest);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByBookerId(bookerId, pageRequest);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByBookerId(bookerId, pageRequest);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return BookingMapper.bookingListToBookingSendDtoList(bookingList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingByOwnerIdAndState(long ownerId, BookingState state, int limit, int offset) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner user not found by id: " + ownerId));
        PageRequest pageRequest = getPageRequest(Sort.Direction.DESC, "start", limit, offset);
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(ownerId, pageRequest);
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastBookingByItemOwnerId(ownerId, pageRequest);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentBookingByItemOwnerId(ownerId, pageRequest);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureBookingByItemOwnerId(ownerId, pageRequest);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookingList = new ArrayList<>();
        }
        return BookingMapper.bookingListToBookingSendDtoList(bookingList);
    }

    private PageRequest getPageRequest(Sort.Direction direction, String sortParam, int limit, int offset) {
        Sort sort = Sort.by(direction, sortParam);
        // Данное решение из "советы ментора", но для соответствия ТЗ я бы делал через nativeQuery = true + order, limit, offset
        // т.к. from/size будет вычислять страницу на которой находится элемент from, а не начинающуюся с элемента from.
        // Например, при "from = 8", а "size = 3" будет вычислено 8 / 3 = 2 -> выдана страница 2 с элементами [6,7,8], а нужно по ТЗ [8,9,10]
        return PageRequest.of((offset / limit), limit, sort);
    }
}
