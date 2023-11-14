package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST \"/item\" Body={}, Headers:(X-Sharer-User-Id)={}", itemDto, userId);
        ItemDto itemToReturn = itemService.create(itemDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PUT \"/item/" + id + "\" Body={}, Headers:(X-Sharer-User-Id)={}", itemDto, userId);
        itemDto.setId(id);
        ItemDto itemToReturn = itemService.update(itemDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @GetMapping
    public List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET \"/item\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemDto> listToReturn = itemService.getByUserId(userId);
        log.debug(listToReturn.toString());
        return listToReturn;
    }

    @GetMapping("/{id}")
    public ItemDto getByItemId(@PathVariable Long id,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET \"/item/" + id + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        ItemDto itemReturn = itemService.getByItemId(id);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @GetMapping("/search")
    public List<ItemDto> getViaSubstringSearch(@RequestParam String text,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET \"/item/search?text=" + text + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemDto> itemReturn = itemService.getViaSubstringSearch(text);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@PathVariable Long itemId,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("DELETE \"/item/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        itemService.deleteByUserIdAndItemId(itemId, userId);
    }
}