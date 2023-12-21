package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemSendDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Builder
public class ItemRequestSendDto {
    private long id;
    private String description;
    private LocalDateTime created;
    // items == responses
    private List<ItemSendDto> items;
}