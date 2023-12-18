package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public static Item createItemFromItemDtoAndOwner(ItemReceiveDto itemReceiveDto, User owner) {
        return Item.builder()
                .id(null)
                .owner(owner)
                .name(itemReceiveDto.getName())
                .description(itemReceiveDto.getDescription())
                .isAvailable(itemReceiveDto.getAvailable())
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

    public static List<ItemReceiveDto> itemReceiveDtoListFromItemList(List<Item> itemList) {
        List<ItemReceiveDto> itemReceiveDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemReceiveDtoList.add(itemReceiveDtoFromItem(item));
        }

        return itemReceiveDtoList;
    }

    public static ItemSendDto itemSendDtoFromItem(Item item) {
        return ItemSendDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static List<ItemSendDto> itemSendDtoListFromItemList(List<Item> itemList) {
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
