package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found by Id " + ownerId));

        Item itemToCreate = itemMapper.createItemFromItemDtoAndOwner(itemDto, owner);
        Item returnedItem = itemRepository.save(itemToCreate);
        itemDto.setId(returnedItem.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, long ownerId) {
        long itemId = itemDto.getId();

        // Check if repository has item with same id
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by id: " + itemId));
        // Check if repository has owner with same id who has same item
        itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " doesn't have item with id " + itemId));

        itemMapper.updateItemByItemDtoNotNullFields(itemDto, item);
        itemRepository.save(item);
        return itemMapper.createItemDtoFromItem(item);
    }

    @Override
    public List<ItemDto> getByOwnerId(long userId) {
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        return itemMapper.createItemDtoListFromItemList(itemList);
    }

    @Override
    public ItemDto getByItemId(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found by Id " + itemId));
        return itemMapper.createItemDtoFromItem(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        String correctText = text.toLowerCase();
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.searchAvailableByNameOrDescription(correctText);
        return itemMapper.createItemDtoListFromItemList(itemList);
    }

    @Override
    public void deleteByItemId(long itemId, long ownerId) {
        itemRepository.deleteByIdAndOwnerId(itemId, ownerId);
    }
}
