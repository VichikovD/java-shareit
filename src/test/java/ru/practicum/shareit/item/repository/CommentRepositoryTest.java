package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CommentRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CommentRepository commentRepository;

    private static final Timestamp CREATED = Timestamp.from(Instant.now());

    @BeforeEach
    public void beforeEach() {
        User owner = User.builder()
                .id(null)
                .email("owner@email.com")
                .name("owner")
                .build();
        entityManager.persist(owner);

        Item item1 = Item.builder()
                .id(null)
                .owner(owner)
                .name("1itemName")
                .description("1itemDescription")
                .isAvailable(true)
                .itemRequest(null)
                .build();
        entityManager.persist(item1);

        Item item2 = Item.builder()
                .id(null)
                .owner(owner)
                .name("2itemName")
                .description("2itemDescription")
                .isAvailable(true)
                .itemRequest(null)
                .build();
        entityManager.persist(item2);

        User author = User.builder()
                .id(null)
                .email("author@email.com")
                .name("author")
                .build();
        entityManager.persist(author);

        Comment comment1 = Comment.builder()
                .id(null)
                .text("1text")
                .creationDate(CREATED)
                .author(author)
                .item(item1)
                .author(author)
                .build();
        entityManager.persist(comment1);

        Comment comment2 = Comment.builder()
                .id(null)
                .text("2text")
                .creationDate(Timestamp.from(Instant.ofEpochSecond(1)))
                .author(author)
                .item(item2)
                .author(author)
                .build();
        entityManager.persist(comment1);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void findAllCommentsInIdList() {
        List<Long> commentIdList = List.of(1L, 3L);

        List<Comment> commentList = commentRepository.findAllCommentsInIdList(commentIdList);
        Comment comment = commentList.get(0);

        assertThat(commentList.size(), is(1));
        assertThat(comment.getId(), is(1L));
        assertThat(comment.getText(), is("1text"));
        assertThat(comment.getCreationDate(), notNullValue());
        assertThat(comment.getItem().getId(), is(1L));
        assertThat(comment.getAuthor().getId(), is(2L));
    }

    private User getOwner() {
        return User.builder()
                .id(null)
                .email("owner@email.com")
                .name("ownerName")
                .build();
    }
}