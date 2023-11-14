package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemDaoImpl implements ItemDao {
    HashMap<Long, HashMap<Long, Item>> items;

    Long idCounter = 0L;

    public ItemDaoImpl() {
        this.items = new HashMap<>();
    }

    private Long getNewId() {
        return ++idCounter;
    }

    @Override
    public Item create(Item item, Long userId) {
        Long id = getNewId();
        item.setId(id);
        HashMap<Long, Item> userItemsMap = items.getOrDefault(userId, new HashMap<>());
        userItemsMap.put(id, item);
        items.put(userId, userItemsMap);
        return item;
    }

    @Override
    public void update(Item item, Long userId) {
        HashMap<Long, Item> userItemsMap = items.get(userId);
        userItemsMap.put(item.getId(), item);
        items.put(userId, userItemsMap);
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return new ArrayList<>(items.get(userId).values());
    }

    @Override
    public Optional<Item> getByItemId(Long itemId) {
        for (HashMap<Long, Item> map : items.values()) {
            for (Item item : map.values()) {
                if (Objects.equals(item.getId(), itemId)) {
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> getByUserIdAndItemId(Long userId, Long itemId) {
        return Optional.ofNullable(items.getOrDefault(userId, new HashMap<>())
                .get(itemId));
    }

    @Override
    public List<Item> getViaSubstringSearch(String text) {
        List<Item> searchResult = new ArrayList<>();
        for (HashMap<Long, Item> map : items.values()) {
            for (Item item : map.values()) {
                if (itemContainsSubstring(item, text)) {
                    searchResult.add(item);
                }
            }
        }
        return searchResult;
    }

    @Override
    public void deleteByUserIdAndItemId(Long itemId, Long userId) {
        HashMap<Long, Item> userItemsMap = items.getOrDefault(userId, new HashMap<>());
        userItemsMap.remove(itemId);
    }

    private boolean itemContainsSubstring(Item item, String text) {
        String name = item.getName()
                .toLowerCase();
        String description = item.getDescription()
                .toLowerCase();
        Boolean available = item.getAvailable();

        return (!text.isBlank() && (name.contains(text) || description.contains(text)) && available);
    }
}
