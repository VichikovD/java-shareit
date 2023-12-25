package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private static final LocalDateTime CREATED = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        User requestingUser = User.builder()
                .id(null)
                .email("requestingUser@email.com")
                .name("name")
                .build();
        entityManager.persist(requestingUser);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(null)
                .requestingUser(requestingUser)
                .description("1requestDescription")
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(itemRequest1);

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(null)
                .requestingUser(requestingUser)
                .description("2requestDescription")
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(itemRequest2);

        ItemRequest itemRequest3 = ItemRequest.builder()
                .id(null)
                .requestingUser(requestingUser)
                .description("3requestDescription")
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(itemRequest3);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getAllWithOffsetAndLimit() {
        int offset = 1;
        int limit = 1;

        List<ItemRequest> itemRequestList = itemRequestRepository.getAllWithOffsetAndLimit(2L, limit, offset);
        ItemRequest actualItemRequest = itemRequestList.get(0);

        assertThat(itemRequestList.size(), is(1));
        assertThat(actualItemRequest.getId(), is(2L));
        assertThat(actualItemRequest.getRequestingUser().getId(), is(1L));
        assertThat(actualItemRequest.getDescription(), is("2requestDescription"));
        assertThat(actualItemRequest.getCreated(), notNullValue());
    }
}