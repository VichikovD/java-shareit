package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + ownerId));

        Item itemToCreate = ItemMapper.createItemFromItemDtoAndOwner(itemDto, owner);
        Item returnedItem = itemRepository.save(itemToCreate);
        itemDto.setId(returnedItem.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, long ownerId) {
        long itemId = itemDto.getId();

        // Check if repository has user with same id
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + ownerId));
        // Check if repository has owner with same id who has same item
        Item item = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " doesn't have item with id " + itemId));

        ItemMapper.updateItemByItemDtoNotNullFields(itemDto, item);
        itemRepository.save(item);
        return ItemMapper.createItemDtoFromItem(item);
    }

    @Override
    public List<ItemDto> getByOwnerId(long userId) {
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderById(userId);
        List<ItemDto> itemDtoList = ItemMapper.createItemDtoListFromItemList(itemList);
        setAllLastAndNextBookingToItemDto(itemDtoList);
        return itemDtoList;
    }

    @Override
    public ItemDto getByItemId(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by Id " + itemId));

        ItemDto itemDto = ItemMapper.createItemDtoFromItem(item);
        if (item.getOwner().getId() == userId) {
            setLastAndNextBookingToItemDto(itemDto);
        }
        setCommentsToItemDto(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> search(String text) {
        String correctText = text.toLowerCase();
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.searchAvailableByNameOrDescription(correctText);
        return ItemMapper.createItemDtoListFromItemList(itemList);
    }

    @Override
    public void deleteByItemId(long itemId, long ownerId) {
        itemRepository.deleteByIdAndOwnerId(itemId, ownerId);
    }

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

    private void setLastAndNextBookingToItemDto(ItemDto itemDto) {
        long itemId = itemDto.getId();
        Booking lastBooking = bookingRepository.findLastForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemDto.setLastBooking(BookingMapper.bookingToBookingDtoItem(lastBooking));

        Booking nextBooking = bookingRepository.findNextForDateTime(itemId, LocalDateTime.now())
                .orElse(null);
        itemDto.setNextBooking(BookingMapper.bookingToBookingDtoItem(nextBooking));
    }

    private void setCommentsToItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        List<CommentDto> commentsDto = CommentMapper.toListCommentDto(comments);
        itemDto.setComments(commentsDto);
    }

    private void setAllLastAndNextBookingToItemDto(Collection<ItemDto> itemDtoList) {
        List<Long> itemIdList = itemDtoList.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<Booking> lastBookingList = bookingRepository.findAllLastForDateTime(itemIdList, LocalDateTime.now());
        for (ItemDto itemDto : itemDtoList) {
            Booking lastBooking = lastBookingList.stream()
                    .filter(booking -> itemDto.getId().equals(booking.getItem().getId()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            itemDto.setLastBooking(BookingMapper.bookingToBookingDtoItem(lastBooking));
        }

        List<Booking> nextBookingList = bookingRepository.findAllNextForDateTime(itemIdList, LocalDateTime.now());
        for (ItemDto itemDto : itemDtoList) {
            Booking nextBooking = nextBookingList.stream()
                    .filter(booking -> itemDto.getId().equals(booking.getItem().getId()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            itemDto.setNextBooking(BookingMapper.bookingToBookingDtoItem(nextBooking));
        }
    }

    private void setAllCommentsToItemDto(Collection<ItemDto> itemDtoList) {
        List<Long> itemIdList = itemDtoList.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<Comment> allCommentsList = commentRepository.findAllCommentsInIdList(itemIdList);
        for (ItemDto itemDto : itemDtoList) {
            List<Comment> commentsListForId = allCommentsList.stream()
                    .filter(comment -> itemDto.getId().equals(comment.getItem().getId()))
                    .collect(Collectors.toList());
            itemDto.setComments(CommentMapper.toListCommentDto(commentsListForId));
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
