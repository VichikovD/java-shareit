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
@ToString
@Builder
@EqualsAndHashCode
public class ItemReceiveDto {
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

