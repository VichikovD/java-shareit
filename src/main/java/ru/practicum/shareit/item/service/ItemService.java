package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;

import java.util.List;

public interface ItemService {
    ItemSendDto create(ItemReceiveDto item, long userId);

    ItemSendDto update(ItemReceiveDto item, long userId);

    List<ItemSendDto> getByOwnerId(long userId, Pageable pageable);

    ItemSendDto getByItemId(long itemId, long userId);

    List<ItemSendDto> search(String text, Pageable pageable);

    void deleteByItemId(long itemId, long ownerId);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}
