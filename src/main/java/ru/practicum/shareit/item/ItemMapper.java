package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public Item createItemFromItemDtoAndOwner(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(null)
                .owner(owner)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .build();
    }

    public ItemDto createItemDtoFromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public List<ItemDto> createItemDtoListFromItemList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(createItemDtoFromItem(item));
        }

        return itemDtoList;
    }

    public void updateItemByItemDtoNotNullFields(ItemDto itemDto, Item item) {
        String name = itemDto.getName();
        if (name != null) {
            item.setName(name);
        }

        String description = itemDto.getDescription();
        if (description != null) {
            item.setDescription(description);
        }

        Boolean available = itemDto.getAvailable();
        if (available != null) {
            item.setIsAvailable(available);
        }
    }
}
