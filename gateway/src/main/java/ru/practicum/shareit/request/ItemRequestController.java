package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestRequestingDto itemRequestDto,
                                         @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("POST \"/requests\" Body={}, Headers:(X-Sharer-User-Id)={}", itemRequestDto, userId);
        ResponseEntity<Object> itemRequestInfoDto = itemRequestClient.create(userId, itemRequestDto);
        log.debug(itemRequestInfoDto.toString());
        return itemRequestInfoDto;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("GET \"/requests/{}\" , Headers:(X-Sharer-User-Id)={}", requestId, userId);
        ResponseEntity<Object> itemRequestInfoDto = itemRequestClient.getById(requestId, userId);
        log.debug(itemRequestInfoDto.toString());
        return itemRequestInfoDto;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestingUserId(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId) {
        log.info("GET \"/requests\" , Headers:(X-Sharer-User-Id)={}", requestingUserId);
        ResponseEntity<Object> listItemRequestInfoDto = itemRequestClient.getAllByRequestingUserId(requestingUserId);
        log.debug(listItemRequestInfoDto.toString());
        return listItemRequestInfoDto;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithOffsetAndLimit(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId,
                                                           @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int size) {
        log.info("GET \"/requests/all?from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", from, size, requestingUserId);
        ResponseEntity<Object> listItemRequestInfoDto = itemRequestClient.getAllWithOffsetAndLimit(requestingUserId, from, size);
        log.debug(listItemRequestInfoDto.toString());
        return listItemRequestInfoDto;
    }
}
