package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public static ItemRequest fromItemRequestReceiveDtoAndRequestingUser(ItemRequestReceiveDto dto, User requestingUser) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .requestingUser(requestingUser)
                .build();
    }

    // without List<Item> itemResponses
    public static ItemRequestSendDto toItemRequestSendDto(ItemRequest itemRequest) {
        return ItemRequestSendDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }

    // without List<Item> itemResponses
    public static List<ItemRequestSendDto> toItemRequestSendDtoList(List<ItemRequest> itemRequestList) {
        List<ItemRequestSendDto> itemRequestSendDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            itemRequestSendDtoList.add(toItemRequestSendDto(itemRequest));
        }
        return itemRequestSendDtoList;
    }
}
