package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestingDto {
    //  Requesting чтобы при создании dto для Item, не получился ItemRequestDto,
    //  который можно спутать с сущностью ItemRequest
    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Item name should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item name should not be empty")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Item description should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item description should not be empty")
    private String description;
    @NotNull(groups = OnCreate.class, message = "Item available status should not be empty")
    private Boolean available;

    private Long requestId;
}

