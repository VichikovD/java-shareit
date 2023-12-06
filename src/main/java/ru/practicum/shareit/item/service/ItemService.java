package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto item, long userId);

    List<ItemDto> getByOwnerId(long userId);

    ItemDto getByItemId(long itemId, long userId);

    List<ItemDto> search(String text);

    void deleteByItemId(long itemId, long ownerId);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}