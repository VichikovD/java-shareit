package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestReceiveDto;
import ru.practicum.shareit.request.dto.ItemRequestSendDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestSendDto create(ItemRequestReceiveDto requestReceiveDto, long userId);

    ItemRequestSendDto getById(long itemRequestId, long userId);

    List<ItemRequestSendDto> getByAllByRequestingUserId(long requestingUserId);

    List<ItemRequestSendDto> getAllWithOffsetAndLimit(long requestingUserId, long offset, long limit);

}
