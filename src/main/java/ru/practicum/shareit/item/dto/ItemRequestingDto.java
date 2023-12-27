package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestingDto {
    //  Requesting чтобы при создании dto для Item, не получился ItemRequestingDto,
    //  который можно спутать с Dto для сущности ItemRequest
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequestingDto that = (ItemRequestingDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

