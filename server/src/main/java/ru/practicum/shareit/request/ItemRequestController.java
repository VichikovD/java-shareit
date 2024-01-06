package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;

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
    public ItemRequestInfoDto create(@RequestBody @Valid ItemRequestRequestingDto requestReceiveDto,
                                     @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("POST \"/requests\" Body={}, Headers:(X-Sharer-User-Id)={}", requestReceiveDto, userId);
        ItemRequestInfoDto itemRequestInfoDto = itemRequestService.create(requestReceiveDto, userId);
        log.debug(itemRequestInfoDto.toString());
        return itemRequestInfoDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        log.info("POST \"/requests/{}\" , Headers:(X-Sharer-User-Id)={}", requestId, userId);
        ItemRequestInfoDto itemRequestInfoDto = itemRequestService.getById(requestId, userId);
        log.debug(itemRequestInfoDto.toString());
        return itemRequestInfoDto;
    }

    @GetMapping
    public List<ItemRequestInfoDto> getByAllByRequestingUserId(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId) {
        log.info("POST \"/requests\" , Headers:(X-Sharer-User-Id)={}", requestingUserId);
        List<ItemRequestInfoDto> listItemRequestInfoDto = itemRequestService.getByAllByRequestingUserId(requestingUserId);
        log.debug(listItemRequestInfoDto.toString());
        return listItemRequestInfoDto;
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllWithOffsetAndLimit(@RequestHeader(name = "X-Sharer-User-Id") long requestingUserId,
                                                             @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) int limit,
                                                             @RequestParam(name = "from", defaultValue = "0") @Min(value = 0) int offset) {
        log.info("POST \"/requests/all?from={}&size={}\" , Headers:(X-Sharer-User-Id)={}", offset, limit, requestingUserId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of((offset / limit), limit, sort);
        List<ItemRequestInfoDto> listItemRequestInfoDto = itemRequestService.getAllWithOffsetAndLimit(requestingUserId, pageable);
        log.debug(listItemRequestInfoDto.toString());
        return listItemRequestInfoDto;
    }
}
