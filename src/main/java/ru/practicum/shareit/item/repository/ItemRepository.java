package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOwnerId(long ownerId, long itemId);

    List<Item> findAllByOwnerId(long ownerId, Sort sort);

    void deleteByIdAndOwnerId(long itemId, long ownerId);

    // List<Item> findAllByIsAvailableTrueAndNameContainingIgnoreCaseOrIsAvailableTrueAndDescriptionContainingIgnoreCase(String correctText, String correctText1);

    @Query(value = "SELECT item " +
            "FROM Item AS item " +
            "WHERE item.isAvailable = true AND (LOWER(item.description) LIKE %?1% OR LOWER(item.name) LIKE %?1%) ")
    List<Item> searchAvailableByNameOrDescription(String substring);
}
