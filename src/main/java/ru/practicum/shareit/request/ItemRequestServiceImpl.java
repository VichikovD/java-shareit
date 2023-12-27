package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemSendDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestReceiveDto;
import ru.practicum.shareit.request.dto.ItemRequestSendDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestSendDto create(ItemRequestReceiveDto requestReceiveDto, long userId) {
        User requestingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestReceiveDtoAndRequestingUser(requestReceiveDto, requestingUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestSendDto(savedItemRequest);
    }

    @Override
    public ItemRequestSendDto getById(long itemRequestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found by id: " + itemRequestId));

        ItemRequestSendDto itemRequestSendDto = ItemRequestMapper.toItemRequestSendDto(itemRequest);
        setResponsesToItemRequestSendDto(itemRequestSendDto);
        return itemRequestSendDto;
    }

    @Override
    public List<ItemRequestSendDto> getByAllByRequestingUserId(long requestingUserId) {
        userRepository.findById(requestingUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + requestingUserId));

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestingUserId(requestingUserId);
        List<ItemRequestSendDto> itemRequestSendDtoList = ItemRequestMapper.toItemRequestSendDtoList(itemRequestList);
        setResponsesToAllItemRequestSendDto(itemRequestSendDtoList);
        return itemRequestSendDtoList;
    }

    @Override
    public List<ItemRequestSendDto> getAllWithOffsetAndLimit(long requestingUserId, Pageable pageable) {
        userRepository.findById(requestingUserId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + requestingUserId));

        List<ItemRequest> itemRequestList = itemRequestRepository.getAllWithOffsetAndLimit(requestingUserId, pageable);
        List<ItemRequestSendDto> itemRequestSendDtoList = ItemRequestMapper.toItemRequestSendDtoList(itemRequestList);
        setResponsesToAllItemRequestSendDto(itemRequestSendDtoList);
        return itemRequestSendDtoList;
    }

    private void setResponsesToItemRequestSendDto(ItemRequestSendDto itemRequestSendDto) {
        long itemRequestId = itemRequestSendDto.getId();
        List<Item> itemResponses = itemRepository.findAllByItemRequestId(itemRequestId);
        itemRequestSendDto.setItems(ItemMapper.itemSendDtoListFromItemList(itemResponses));
    }

    private void setResponsesToAllItemRequestSendDto(Collection<ItemRequestSendDto> itemRequestSendDtoCollection) {
        List<Long> itemRequestIdList = itemRequestSendDtoCollection.stream()
                .map(ItemRequestSendDto::getId)
                .collect(Collectors.toList());
        List<Item> allItemsInRequestIdList = itemRepository.findAllByItemRequestIdIn(itemRequestIdList);

        Map<Long, List<Item>> requestIdToItemListMap = allItemsInRequestIdList
                .stream()
                .collect(Collectors.groupingBy((item) -> item.getItemRequest().id));

        for (ItemRequestSendDto requestSendDto : itemRequestSendDtoCollection) {
            List<ItemSendDto> responses = ItemMapper.itemSendDtoListFromItemList(
                    requestIdToItemListMap.getOrDefault(requestSendDto.getId(), new ArrayList<>()));
            requestSendDto.setItems(responses);
        }
    }
}
