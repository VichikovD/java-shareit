package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.CommentRequestingDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemRequestingDto;

import java.util.List;

public interface ItemService {
    ItemInfoDto create(ItemRequestingDto item, long userId);

    ItemInfoDto update(ItemRequestingDto item, long userId);

    List<ItemInfoDto> getByOwnerId(long userId, Pageable pageable);

    ItemInfoDto getByItemId(long itemId, long userId);

    List<ItemInfoDto> search(String text, Pageable pageable);

    void deleteByItemId(long itemId, long ownerId);

    CommentInfoDto createComment(CommentRequestingDto commentRequestingDto, long itemId, long userId);
}
