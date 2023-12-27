package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentRequestingDto {
    //  Requesting чтобы при создании dto для Item, не получился ItemRequestDto,
    //  который можно спутать с сущностью ItemRequest
    Long id;

    @NotBlank(message = "Text fromString comment should not be empty or null")
    String text;

    String authorName;

    LocalDateTime created;

    @JsonIgnore
    Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentRequestingDto that = (CommentRequestingDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
