package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestReceiveDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ItemMapper {
    public static Item createItemFromItemDtoAndOwnerAndItemReceive(ItemReceiveDto itemReceiveDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(null)
                .owner(owner)
                .name(itemReceiveDto.getName())
                .description(itemReceiveDto.getDescription())
                .isAvailable(itemReceiveDto.getAvailable())
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemReceiveDto itemReceiveDtoFromItem(Item item) {
        return ItemReceiveDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static ItemSendDto itemSendDtoFromItem(Item item) {
        Long itemRequestId = null;
        ItemRequest itemRequest = item.getItemRequest();
        if(itemRequest != null){
            itemRequestId = itemRequest.getId();
        }
        return ItemSendDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(itemRequestId)
                .build();
    }

    public static List<ItemSendDto> itemSendDtoListFromItemList(Collection<Item> itemList) {
        List<ItemSendDto> itemSendDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemSendDtoList.add(itemSendDtoFromItem(item));
        }

        return itemSendDtoList;
    }

    public static void updateItemByItemDtoNotNullFields(ItemReceiveDto itemReceiveDto, Item item) {
        String name = itemReceiveDto.getName();
        if (name != null) {
            item.setName(name);
        }

        String description = itemReceiveDto.getDescription();
        if (description != null) {
            item.setDescription(description);
        }

        Boolean available = itemReceiveDto.getAvailable();
        if (available != null) {
            item.setIsAvailable(available);
        }
    }
}
