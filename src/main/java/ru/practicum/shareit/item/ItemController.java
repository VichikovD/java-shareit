package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemReceiveDto;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.service.ItemService;

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
    public ItemSendDto create(@RequestBody @Validated(OnCreate.class) ItemReceiveDto itemReceiveDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items\" Body={}, Headers:(X-Sharer-User-Id)={}", itemReceiveDto, userId);
        ItemSendDto itemToReturn = itemService.create(itemReceiveDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @PatchMapping("/{itemId}")
    public ItemSendDto update(@RequestBody @Validated(OnUpdate.class) ItemReceiveDto itemReceiveDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PUT \"/items/" + itemId + "\" Body={}, Headers:(X-Sharer-User-Id)={}", itemReceiveDto, userId);
        itemReceiveDto.setId(itemId);
        ItemSendDto itemToReturn = itemService.update(itemReceiveDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @GetMapping
    public List<ItemSendDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/items\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemSendDto> listToReturn = itemService.getByOwnerId(userId);
        log.debug(listToReturn.toString());
        return listToReturn;
    }

    @GetMapping("/{itemId}")
    public ItemSendDto getByItemId(@PathVariable long itemId,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/items/" + itemId + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        ItemSendDto itemReturn = itemService.getByItemId(itemId, userId);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @GetMapping("/search")
    public List<ItemSendDto> getViaSubstringSearch(@RequestParam String text,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/items/search?text=" + text + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        List<ItemSendDto> itemReturn = itemService.search(text);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@PathVariable long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("DELETE \"/items/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        itemService.deleteByItemId(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestBody @Validated CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items/{}/comment\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, commentDto, userId);
        CommentDto commentToReturn = itemService.createComment(commentDto, itemId, userId);
        log.debug(commentToReturn.toString());
        return commentToReturn;
    }
}