package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long userId);

    ItemDto update(ItemDto item, Long userId);

    List<ItemDto> getByUserId(Long userId);

    ItemDto getByItemId(Long userId);

    List<ItemDto> getViaSubstringSearch(String text);

    void deleteByUserIdAndItemId(Long itemId, Long userId);
}
