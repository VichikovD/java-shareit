package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item create(Item item, Long userId);

    void update(Item item, Long userId);

    List<Item> getByUserId(Long userId);

    Optional<Item> getByItemId(Long userId);

    Optional<Item> getByUserIdAndItemId(Long userId, Long itemId);

    List<Item> getViaSubstringSearch(String text);

    void deleteByUserIdAndItemId(Long itemId, Long userId);
}
