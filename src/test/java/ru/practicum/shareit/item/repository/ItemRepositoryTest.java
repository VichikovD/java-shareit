package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    public void beforeEach() {
    }

    @Test
    void searchAvailableByNameOrDescription_whenNoCoincided_thenReturnEmptyList() {
        User owner = User.builder()
                .id(null)
                .email("owner@email.com")
                .name("ownerName")
                .build();
        entityManager.persist(owner);
        owner.setId(1L);
        int offset = 0;
        int limit = 1;
        PageRequest pageRequest = PageRequest.of((offset / limit), limit, Sort.by(Sort.Direction.ASC, "id"));
        Item item1 = new Item(null, owner, "1name", "1description", true, null);
        entityManager.persist(item1);
        Item item2 = new Item(null, owner, "other", "other", true, null);
        entityManager.persist(item2);

        List<Item> foundItems = itemRepository.searchAvailableByNameOrDescription("descript", pageRequest);
        Item itemFound = foundItems.get(0);

        assertThat(foundItems.size(), is(1));
        assertThat(itemFound.getId(), is(1L));
        assertThat(itemFound.getName(), is("1name"));
        assertThat(itemFound.getDescription(), is("1description"));
        assertThat(itemFound.getIsAvailable(), is(true));
        assertThat(itemFound.getItemRequest(), nullValue());
    }
}