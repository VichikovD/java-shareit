package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequestingDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Override
    public ItemRequestInfoDto create(ItemRequestRequestingDto requestReceiveDto, long userId) {
        User requestingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest itemRequest = ItemRequestMapper.toModel(requestReceiveDto, requestingUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestInfoDto(savedItemRequest);
    }

    @Override
    public ItemRequestInfoDto getById(long itemRequestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found by id: " + itemRequestId));

        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest);
        setResponsesToItemRequestSendDto(itemRequestInfoDto);
        return itemRequestInfoDto;
    }

    @Override
    public List<ItemRequestInfoDto> getByAllByRequestingUserId(long requestingUserId) {
        userRepository.findById(requestingUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + requestingUserId));

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestingUserId(requestingUserId);
        List<ItemRequestInfoDto> itemRequestInfoDtoList = ItemRequestMapper.toItemRequestInfoDtoList(itemRequestList);
        setResponsesToAllItemRequestSendDto(itemRequestInfoDtoList);
        return itemRequestInfoDtoList;
    }

    @Override
    public List<ItemRequestInfoDto> getAllWithOffsetAndLimit(long requestingUserId, Pageable pageable) {
        userRepository.findById(requestingUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + requestingUserId));

        List<ItemRequest> itemRequestList = itemRequestRepository.getAllWithOffsetAndLimit(requestingUserId, pageable);
        List<ItemRequestInfoDto> itemRequestInfoDtoList = ItemRequestMapper.toItemRequestInfoDtoList(itemRequestList);
        setResponsesToAllItemRequestSendDto(itemRequestInfoDtoList);
        return itemRequestInfoDtoList;
    }

    private void setResponsesToItemRequestSendDto(ItemRequestInfoDto itemRequestInfoDto) {
        long itemRequestId = itemRequestInfoDto.getId();
        List<Item> itemResponses = itemRepository.findAllByItemRequestId(itemRequestId);
        itemRequestInfoDto.setItems(ItemMapper.toItemInfoDtoList(itemResponses));
    }

    private void setResponsesToAllItemRequestSendDto(Collection<ItemRequestInfoDto> itemRequestInfoDtoCollection) {
        List<Long> itemRequestIdList = itemRequestInfoDtoCollection.stream()
                .map(ItemRequestInfoDto::getId)
                .collect(Collectors.toList());
        List<Item> allItemsInRequestIdList = itemRepository.findAllByItemRequestIdIn(itemRequestIdList);

        Map<Long, List<Item>> requestIdToItemListMap = allItemsInRequestIdList
                .stream()
                .collect(Collectors.groupingBy((item) -> item.getItemRequest().id));

        for (ItemRequestInfoDto requestSendDto : itemRequestInfoDtoCollection) {
            List<ItemInfoDto> responses = ItemMapper.toItemInfoDtoList(
                    requestIdToItemListMap.getOrDefault(requestSendDto.getId(), new ArrayList<>()));
            requestSendDto.setItems(responses);
        }
    }
}
