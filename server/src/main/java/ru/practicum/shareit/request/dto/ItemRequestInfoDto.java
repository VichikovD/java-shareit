package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestInfoDto {
    private long id;
    private String description;
    private LocalDateTime created;
    // items == responses
    private List<ItemInfoDto> items;
}
