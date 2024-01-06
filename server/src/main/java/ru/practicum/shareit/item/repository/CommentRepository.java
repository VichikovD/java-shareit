package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(long itemId);

    @Query(value = "SELECT c " +
            "FROM Comment AS c " +
            "WHERE c.item.id in ?1 ")
    List<Comment> findAllCommentsInIdList(List<Long> idList);
}
