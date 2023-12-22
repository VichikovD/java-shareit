package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
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
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final ItemRequestRepository itemRequestRepository;
    private static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort SORT_START_ASC = Sort.by(Sort.Direction.ASC, "start");


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional
    @Override
    public ItemSendDto create(ItemReceiveDto itemReceiveDto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));

        ItemRequest itemRequest = null;
        Long itemRequestId = itemReceiveDto.getRequestId();
        if (itemRequestId != null) {
            itemRequest = itemRequestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new NotFoundException("ItemRequest not found by id: " + itemRequestId));
        }

        Item itemToCreate = ItemMapper.createItemFromItemDtoAndOwnerAndItemReceive(itemReceiveDto, owner, itemRequest);

        Item returnedItem = itemRepository.save(itemToCreate);
        return ItemMapper.itemSendDtoFromItem(returnedItem);
    }

    @Transactional
    @Override
    public ItemSendDto update(ItemReceiveDto itemReceiveDto, long ownerId) {
        long itemId = itemReceiveDto.getId();

        // Check if repository has user with same id
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));
        // Check if repository has owner with same id who has same item
        Item item = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + ownerId + " doesn't have item with id " + itemId));

        ItemMapper.updateItemByItemDtoNotNullFields(itemReceiveDto, item);
        itemRepository.save(item);
        return ItemMapper.itemSendDtoFromItem(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemSendDto> getByOwnerId(long userId, int limit, int offset) {
        PageRequest pageRequest = getPageRequest(Sort.Direction.ASC, "id", limit, offset);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<ItemSendDto> itemSendDtoList = ItemMapper.itemSendDtoListFromItemList(itemList);

        setAllLastAndNextBookingToItemDto(itemSendDtoList);
        setAllCommentsToItemSendDto(itemSendDtoList);
        return itemSendDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemSendDto getByItemId(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id " + itemId));

        ItemSendDto itemSendDto = ItemMapper.itemSendDtoFromItem(item);
        if (item.getOwner().getId() == userId) {
            setLastAndNextBookingToItemDto(itemSendDto);
        }
        setCommentsToItemDto(itemSendDto);
        return itemSendDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemSendDto> search(String text, int limit, int offset) {
        PageRequest pageRequest = getPageRequest(Sort.Direction.ASC, "id", limit, offset);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String correctText = text.toLowerCase();
        List<Item> itemList = itemRepository.searchAvailableByNameOrDescription(correctText, pageRequest);
        return ItemMapper.itemSendDtoListFromItemList(itemList);
    }

    @Transactional
    @Override
    public void deleteByItemId(long itemId, long ownerId) {
        itemRepository.deleteByIdAndOwnerId(itemId, ownerId);
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));
        validatePossibilityToComment(userId, itemId);

        Comment comment = CommentMapper.fromCommentDto(commentDto, item, user);
        Comment commentToReturn = commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentToReturn);
    }

    private PageRequest getPageRequest(Sort.Direction direction, String sortParam, int limit, int offset) {
        Sort sort = Sort.by(direction, sortParam);
        // Данное решение из "советы ментора", но для соответствия тз я бы делал через нативные запросы + order, limit, offset
        return PageRequest.of((offset / limit), limit, sort);
    }

    private void setLastAndNextBookingToItemDto(ItemSendDto itemSendDto) {
        long itemId = itemSendDto.getId();
        Booking lastBooking = bookingRepository.findLastForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemSendDto.setLastBooking(ItemSendDto.bookingToBookingDtoItem(lastBooking));

        Booking nextBooking = bookingRepository.findNextForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemSendDto.setNextBooking(ItemSendDto.bookingToBookingDtoItem(nextBooking));
    }

    private void setCommentsToItemDto(ItemSendDto itemSendDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemSendDto.getId());
        List<CommentDto> commentsDto = CommentMapper.toCommentDtoList(comments);
        itemSendDto.setComments(commentsDto);
    }

    private void setAllLastAndNextBookingToItemDto(Collection<ItemSendDto> itemSendDtoList) {
        List<Long> itemIdList = itemSendDtoList.stream()
                .map(ItemSendDto::getId)
                .collect(Collectors.toList());

        List<Booking> lastBookingList = bookingRepository.findAllLastForDateTime(itemIdList, LocalDateTime.now());
        Map<Long, List<Booking>> lastBookingMap = lastBookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        for (ItemSendDto itemSendDto : itemSendDtoList) {
            List<Booking> lastBookings = lastBookingMap.getOrDefault(itemSendDto.getId(), new ArrayList<>());
            Booking lastBooking = lastBookings.stream()
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            itemSendDto.setLastBooking(ItemSendDto.bookingToBookingDtoItem(lastBooking));
        }

        List<Booking> nextBookingList = bookingRepository.findAllNextForDateTime(itemIdList, LocalDateTime.now());
        Map<Long, List<Booking>> nextBookingMap = nextBookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        for (ItemSendDto itemSendDto : itemSendDtoList) {
            List<Booking> nextBookingsList = nextBookingMap.getOrDefault(itemSendDto.getId(), new ArrayList<>());
            Booking nextBooking = nextBookingsList.stream()
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            itemSendDto.setNextBooking(ItemSendDto.bookingToBookingDtoItem(nextBooking));
        }
    }

    private void setAllCommentsToItemSendDto(Collection<ItemSendDto> itemSendDtoList) {
        List<Long> itemIdList = itemSendDtoList.stream()
                .map(ItemSendDto::getId)
                .collect(Collectors.toList());

        List<Comment> allCommentsList = commentRepository.findAllCommentsInIdList(itemIdList);
        Map<Long, List<Comment>> commentsMap = allCommentsList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (ItemSendDto itemSendDto : itemSendDtoList) {
            List<Comment> comments = commentsMap.getOrDefault(itemSendDto.getId(), new ArrayList<>());
            itemSendDto.setComments(CommentMapper.toCommentDtoList(comments));
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
