package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.CommentRequestingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {
    public static CommentInfoDto toInfoDto(Comment comment) {
        return CommentInfoDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(comment.getCreationDate().toLocalDateTime())
                .build();
    }

    public static List<CommentInfoDto> toCommentInfoDtoList(List<Comment> commentList) {
        List<CommentInfoDto> commentInfoDtoList = new ArrayList();
        for (Comment comment : commentList) {
            commentInfoDtoList.add(toInfoDto(comment));
        }
        return commentInfoDtoList;
    }

    public static Comment toModel(CommentRequestingDto commentRequestingDto, Item item, User author) {
        return Comment.builder()
                .id(null)
                .text(commentRequestingDto.getText())
                .author(author)
                .item(item)
                .creationDate(Timestamp.from(Instant.now()))
                .build();
    }
}
