package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

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

}
