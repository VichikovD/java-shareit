package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestReceiveDto;
import ru.practicum.shareit.request.dto.ItemRequestSendDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestSendDto create(@RequestBody @Valid ItemRequestReceiveDto requestReceiveDto,
                                     @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("POST \"/requests\" Body={}, Headers:(X-Sharer-User-Id)={}", requestReceiveDto, userId);
        ItemRequestSendDto itemRequestSendDto = itemRequestService.create(requestReceiveDto, userId);
        log.debug(itemRequestSendDto.toString());
        return itemRequestSendDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestSendDto getById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        log.info("POST \"/requests/{}\" , Headers:(X-Sharer-User-Id)={}", requestId, userId);
        ItemRequestSendDto itemRequestSendDto = itemRequestService.getById(requestId, userId);
        log.debug(itemRequestSendDto.toString());
        return itemRequestSendDto;
    }

    @GetMapping
    public List<ItemRequestSendDto> getByAllByRequestingUserId(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId) {
        log.info("POST \"/requests\" , Headers:(X-Sharer-User-Id)={}", requestingUserId);
        List<ItemRequestSendDto> listItemRequestSendDto = itemRequestService.getByAllByRequestingUserId(requestingUserId);
        log.debug(listItemRequestSendDto.toString());
        return listItemRequestSendDto;
    }

    @GetMapping("/all")
    public List<ItemRequestSendDto> getAllWithOffsetAndLimit(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId,
                                                             @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) Long limit,
                                                             @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) Long offset) {
        log.info("POST \"/requests/all?from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", offset, limit, requestingUserId);
        List<ItemRequestSendDto> listItemRequestSendDto = itemRequestService.getAllWithOffsetAndLimit(requestingUserId, limit, offset);
        log.debug(listItemRequestSendDto.toString());
        return listItemRequestSendDto;
    }
}
