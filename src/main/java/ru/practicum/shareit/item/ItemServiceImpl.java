package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    ItemDao itemDao;
    UserDao userDao;
    ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao, ItemMapper itemMapper) {
        this.itemDao = itemDao;
        this.userDao = userDao;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userDao.getById(userId)
                .orElseThrow(() -> new InvalidIdException("User not found by Id " + userId));

        Item itemToUpdate = itemMapper.createItemFromItemDtoAndOwnerId(itemDto, userId);
        Item returnedItem = itemDao.create(itemToUpdate, userId);
        itemDto.setId(returnedItem.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        Long itemId = itemDto.getId();
        User owner = userDao.getById(userId)
                .orElseThrow(() -> new InvalidIdException("User not found by Id " + userId));
        Item item = itemDao.getByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new InvalidIdException("User with id " + userId + " doesn't have item with id " + itemId));

        itemMapper.updateItemByItemDtoNotNullFields(itemDto, item);
        itemDao.update(item, userId);
        return itemMapper.createItemDtoFromItem(item);
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        List<Item> itemList = itemDao.getByUserId(userId);
        return itemMapper.createItemDtoListFromItemList(itemList);
    }

    @Override
    public ItemDto getByItemId(Long itemId) {
        Item item = itemDao.getByItemId(itemId)
                .orElseThrow(() -> new InvalidIdException("Item not found by Id " + itemId));
        return itemMapper.createItemDtoFromItem(item);
    }

    @Override
    public List<ItemDto> getViaSubstringSearch(String text) {
        List<Item> itemList = itemDao.getViaSubstringSearch(text.toLowerCase());
        return itemMapper.createItemDtoListFromItemList(itemList);
    }

    @Override
    public void deleteByUserIdAndItemId(Long itemId, Long userId) {
        itemDao.deleteByUserIdAndItemId(itemId, userId);
    }
}
