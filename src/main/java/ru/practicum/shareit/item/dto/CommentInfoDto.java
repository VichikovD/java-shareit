package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentInfoDto {
    Long id;

    String text;

    String authorName;

    LocalDateTime created;

    //    @JsonIgnore
    Long itemId;

}
