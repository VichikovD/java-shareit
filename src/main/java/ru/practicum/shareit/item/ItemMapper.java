package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public Item createItemFromItemDtoAndOwnerId(ItemDto itemDto, Long ownerId) {
        return Item.builder()
                .id(null)
                .ownerId(ownerId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public ItemDto createItemDtoFromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
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
            item.setAvailable(available);
        }
    }
}
