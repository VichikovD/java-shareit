package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(comment.getCreationDate().toLocalDateTime())
                .build();
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList) {
        List<CommentDto> commentDtoList = new ArrayList();
        for (Comment comment : commentList) {
            commentDtoList.add(toCommentDto(comment));
        }
        return commentDtoList;
    }

    public static Comment fromCommentDto(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .id(null)
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .creationDate(Timestamp.from(Instant.now()))
                .build();
    }
}
