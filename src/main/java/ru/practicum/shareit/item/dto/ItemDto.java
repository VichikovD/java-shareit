package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.groupMarker.OnCreate;
import ru.practicum.shareit.groupMarker.OnUpdate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.validation.NotEmptyIfNotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Item name should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item name should not be empty")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Item description should not be empty")
    @NotEmptyIfNotNull(groups = OnUpdate.class, message = "Item description should not be empty")
    private String description;

    @NotNull(groups = OnCreate.class, message = "Item available status should not be empty")
    private Boolean available;

    private BookingDtoItem lastBooking;

    private BookingDtoItem nextBooking;

    private List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}