package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestInfoDto create(ItemRequestRequestingDto requestReceiveDto, long userId);

    ItemRequestInfoDto getById(long itemRequestId, long userId);

    List<ItemRequestInfoDto> getByAllByRequestingUserId(long requestingUserId);

    List<ItemRequestInfoDto> getAllWithOffsetAndLimit(long requestingUserId, Pageable pageable);
}
