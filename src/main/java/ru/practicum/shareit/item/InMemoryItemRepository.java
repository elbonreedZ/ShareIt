package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.api.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private long idCounter;
    Map<Long, Item> items = new HashMap<>();
    Map<Long, Set<Item>> ownerItems = new HashMap<>();

    @Override
    public Optional<Item> getById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        ownerItems.computeIfAbsent(item.getOwner(), ownerId -> new HashSet<>()).add(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Set<Item> existedOwnerItems = ownerItems.get(item.getOwner());
        existedOwnerItems.remove(item);
        existedOwnerItems.add(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getByOwner(long ownerId) {
        return new ArrayList<>(ownerItems.get(ownerId));
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text)) && item.isAvailable())
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return ++idCounter;
    }
}
