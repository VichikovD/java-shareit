package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.item.dto.ItemRequestingDto;

import javax.validation.constraints.Min;

@RestController
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(OnCreate.class) ItemRequestingDto itemRequestingDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items\" Body={}, Headers:(X-Sharer-User-Id)={}", itemRequestingDto, userId);
        ResponseEntity<Object> itemToReturn = itemClient.create(userId, itemRequestingDto);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Validated(OnUpdate.class) ItemRequestingDto itemRequestingDto,
                                         @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PUT \"/items/" + itemId + "\" Body={}, Headers:(X-Sharer-User-Id)={}", itemRequestingDto, userId);
        ResponseEntity<Object> itemToReturn = itemClient.update(itemId, userId, itemRequestingDto);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int size) {
        log.info("GET \"/items?from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", from, size, userId);
        ResponseEntity<Object> listToReturn = itemClient.getByOwnerId(userId, from, size);
        log.debug(listToReturn.toString());
        return listToReturn;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/items/" + itemId + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        ResponseEntity<Object> itemReturn = itemClient.getByItemId(itemId, userId);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam String text,
                                         @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int size,
                                         @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int from) {
        log.info("GET \"/items/search?text={}&from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", text, from, size, userId);
        ResponseEntity<Object> itemReturn = itemClient.search(userId, text, from, size);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteByUserIdAndItemId(@PathVariable long itemId,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("DELETE \"/items/{}\" , Headers:(X-Sharer-User-Id)={}", itemId, userId);
        return itemClient.deleteByItemId(itemId, userId);
    }
/*
    @PostMapping("/{itemId}/comment")
    public CommentInfoDto createComment(@PathVariable long itemId,
                                        @RequestBody @Validated CommentRequestingDto commentRequestingDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items/{}/comment\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, commentRequestingDto, userId);
        CommentInfoDto commentToReturn = itemClient.createComment(commentRequestingDto, itemId, userId);
        log.debug(commentToReturn.toString());
        return commentToReturn;
    }*/
}