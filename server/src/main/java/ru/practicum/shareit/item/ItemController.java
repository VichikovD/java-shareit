package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.CommentRequestingDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
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
    public ItemInfoDto create(@RequestBody ItemRequestingDto itemRequestingDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items\" Body={}, Headers:(X-Sharer-User-Id)={}", itemRequestingDto, userId);
        ItemInfoDto itemToReturn = itemService.create(itemRequestingDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @PatchMapping("/{itemId}")
    public ItemInfoDto update(@RequestBody ItemRequestingDto itemRequestingDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PUT \"/items/" + itemId + "\" Body={}, Headers:(X-Sharer-User-Id)={}", itemRequestingDto, userId);
        itemRequestingDto.setId(itemId);
        ItemInfoDto itemToReturn = itemService.update(itemRequestingDto, userId);
        log.debug(itemToReturn.toString());
        return itemToReturn;
    }

    @GetMapping
    public List<ItemInfoDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(name = "size", defaultValue = "10") int limit,
                                          @RequestParam(name = "from", defaultValue = "0") int offset) {
        log.info("GET \"/items?from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", offset, limit, userId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<ItemInfoDto> listToReturn = itemService.getByOwnerId(userId, pageable);
        log.debug(listToReturn.toString());
        return listToReturn;
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getByItemId(@PathVariable long itemId,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET \"/items/" + itemId + "\" , Headers:(X-Sharer-User-Id)={}", userId);
        ItemInfoDto itemReturn = itemService.getByItemId(itemId, userId);
        log.debug(itemReturn.toString());
        return itemReturn;
    }

    @GetMapping("/search")
    public List<ItemInfoDto> search(@RequestParam String text,
                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(name = "size", defaultValue = "10") int limit,
                                    @RequestParam(name = "from", defaultValue = "0") int offset) {
        log.info("GET \"/items/search?text={}&from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", text, limit, offset, userId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<ItemInfoDto> itemReturn = itemService.search(text, pageable);
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
    public CommentInfoDto createComment(@PathVariable long itemId,
                                        @RequestBody CommentRequestingDto commentRequestingDto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST \"/items/{}/comment\" Body={}, Headers:(X-Sharer-User-Id)={}", itemId, commentRequestingDto, userId);
        CommentInfoDto commentToReturn = itemService.createComment(commentRequestingDto, itemId, userId);
        log.debug(commentToReturn.toString());
        return commentToReturn;
    }
}