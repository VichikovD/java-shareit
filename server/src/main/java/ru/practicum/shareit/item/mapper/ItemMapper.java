package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ItemMapper {
    public static Item toModel(ItemRequestingDto itemRequestingDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(null)
                .owner(owner)
                .name(itemRequestingDto.getName())
                .description(itemRequestingDto.getDescription())
                .isAvailable(itemRequestingDto.getAvailable())
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item) {
        Long itemRequestId = null;
        ItemRequest itemRequest = item.getItemRequest();
        if (itemRequest != null) {
            itemRequestId = itemRequest.getId();
        }
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(itemRequestId)
                .build();
    }

    public static List<ItemInfoDto> toItemInfoDtoList(Collection<Item> itemList) {
        List<ItemInfoDto> itemInfoDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemInfoDtoList.add(toItemInfoDto(item));
        }

        return itemInfoDtoList;
    }

    public static void updateItemByItemRequestingDtoNotNullFields(ItemRequestingDto itemRequestingDto, Item item) {
        String name = itemRequestingDto.getName();
        if (name != null) {
            item.setName(name);
        }

        String description = itemRequestingDto.getDescription();
        if (description != null) {
            item.setDescription(description);
        }

        Boolean available = itemRequestingDto.getAvailable();
        if (available != null) {
            item.setIsAvailable(available);
        }
    }
}
