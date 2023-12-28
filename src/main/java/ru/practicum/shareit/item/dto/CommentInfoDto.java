package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentInfoDto {
    Long id;

    @NotBlank(message = "Text fromString comment should not be empty or null")
    String text;

    String authorName;

    LocalDateTime created;

    //    @JsonIgnore
    Long itemId;

}
