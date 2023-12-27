package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.CommentRequestingDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final ItemRequestRepository itemRequestRepository;
    private static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort SORT_START_ASC = Sort.by(Sort.Direction.ASC, "start");


    @Transactional
    @Override
    public ItemInfoDto create(ItemRequestingDto itemRequestingDto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));

        ItemRequest itemRequest = null;
        Long itemRequestId = itemRequestingDto.getRequestId();
        if (itemRequestId != null) {
            itemRequest = itemRequestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new NotFoundException("ItemRequest not found by id: " + itemRequestId));
        }

        Item itemToCreate = ItemMapper.toModel(itemRequestingDto, owner, itemRequest);

        Item returnedItem = itemRepository.save(itemToCreate);
        return ItemMapper.toItemInfoDto(returnedItem);
    }

    @Transactional
    @Override
    public ItemInfoDto update(ItemRequestingDto itemRequestingDto, long ownerId) {
        long itemId = itemRequestingDto.getId();

        // Check if repository has user with same id
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));
        // Check if repository has owner with same id who has same item
        Item item = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + ownerId + " doesn't have item with id " + itemId));

        ItemMapper.updateItemByItemRequestingDtoNotNullFields(itemRequestingDto, item);
        itemRepository.save(item);
        return ItemMapper.toItemInfoDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> getByOwnerId(long userId, Pageable pageable) {
        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);
        List<ItemInfoDto> itemInfoDtoList = ItemMapper.toItemInfoDtoList(itemList);

        setAllLastAndNextBookingToItemDto(itemInfoDtoList);
        setAllCommentsToItemSendDto(itemInfoDtoList);
        return itemInfoDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemInfoDto getByItemId(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id " + itemId));

        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item);
        if (item.getOwner().getId() == userId) {
            setLastAndNextBookingToItemDto(itemInfoDto);
        }
        setCommentsToItemDto(itemInfoDto);
        return itemInfoDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> search(String text, Pageable pageable) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String correctText = text.toLowerCase();
        List<Item> itemList = itemRepository.searchAvailableByNameOrDescription(correctText, pageable);
        return ItemMapper.toItemInfoDtoList(itemList);
    }

    @Transactional
    @Override
    public void deleteByItemId(long itemId, long ownerId) {
        itemRepository.deleteByIdAndOwnerId(itemId, ownerId);
    }

    @Transactional
    @Override
    public CommentInfoDto createComment(CommentRequestingDto commentRequestingDto, long itemId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));
        validatePossibilityToComment(userId, itemId);

        Comment comment = CommentMapper.toModel(commentRequestingDto, item, user);
        Comment commentToReturn = commentRepository.save(comment);
        return CommentMapper.toInfoDto(commentToReturn);
    }

    private PageRequest getPageRequest(Sort.Direction direction, String sortParam, int limit, int offset) {
        Sort sort = Sort.by(direction, sortParam);
        // Данное решение из "советы ментора", но для соответствия тз я бы делал через нативные запросы + order, limit, offset
        return PageRequest.of((offset / limit), limit, sort);
    }

    private void setLastAndNextBookingToItemDto(ItemInfoDto itemInfoDto) {
        long itemId = itemInfoDto.getId();
        Booking lastBooking = bookingRepository.findLastForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemInfoDto.setLastBooking(ItemInfoDto.bookingToBookingDtoItem(lastBooking));

        Booking nextBooking = bookingRepository.findNextForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemInfoDto.setNextBooking(ItemInfoDto.bookingToBookingDtoItem(nextBooking));
    }

    private void setCommentsToItemDto(ItemInfoDto itemInfoDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemInfoDto.getId());
        List<CommentInfoDto> commentsDto = CommentMapper.toCommentInfoDtoList(comments);
        itemInfoDto.setComments(commentsDto);
    }

    private void setAllLastAndNextBookingToItemDto(Collection<ItemInfoDto> itemInfoDtoList) {
        List<Long> itemIdList = itemInfoDtoList.stream()
                .map(ItemInfoDto::getId)
                .collect(Collectors.toList());

        List<Booking> lastBookingList = bookingRepository.findAllLastForDateTime(itemIdList, LocalDateTime.now());
        Map<Long, List<Booking>> lastBookingMap = lastBookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        for (ItemInfoDto itemInfoDto : itemInfoDtoList) {
            List<Booking> lastBookings = lastBookingMap.getOrDefault(itemInfoDto.getId(), new ArrayList<>());
            Booking lastBooking = lastBookings.stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            itemInfoDto.setLastBooking(ItemInfoDto.bookingToBookingDtoItem(lastBooking));
        }

        List<Booking> nextBookingList = bookingRepository.findAllNextForDateTime(itemIdList, LocalDateTime.now());
        Map<Long, List<Booking>> nextBookingMap = nextBookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        for (ItemInfoDto itemInfoDto : itemInfoDtoList) {
            List<Booking> nextBookingsList = nextBookingMap.getOrDefault(itemInfoDto.getId(), new ArrayList<>());
            Booking nextBooking = nextBookingsList.stream()
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            itemInfoDto.setNextBooking(ItemInfoDto.bookingToBookingDtoItem(nextBooking));
        }
    }

    private void setAllCommentsToItemSendDto(Collection<ItemInfoDto> itemInfoDtoList) {
        List<Long> itemIdList = itemInfoDtoList.stream()
                .map(ItemInfoDto::getId)
                .collect(Collectors.toList());

        List<Comment> allCommentsList = commentRepository.findAllCommentsInIdList(itemIdList);
        Map<Long, List<Comment>> commentsMap = allCommentsList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (ItemInfoDto itemInfoDto : itemInfoDtoList) {
            List<Comment> comments = commentsMap.getOrDefault(itemInfoDto.getId(), new ArrayList<>());
            itemInfoDto.setComments(CommentMapper.toCommentInfoDtoList(comments));
        }
    }

    private void validatePossibilityToComment(long userId, long itemId) {
        long bookingQuantity = bookingRepository.countAllPastForItemByTime(itemId, userId, LocalDateTime.now());
        if (bookingQuantity < 1) {
            throw new NotAvailableException("User with id " + userId + " can not comment item with id " + itemId + "" +
                    " due to " + bookingQuantity + " times booked this item in past");
        }
    }
}
