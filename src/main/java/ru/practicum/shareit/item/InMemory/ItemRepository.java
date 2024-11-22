package ru.practicum.shareit.item.InMemory;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getById(long id);

    Item create(Item item);

    Item update(Item item);

    List<Item> getByOwner(long ownerId);

    List<Item> search(String text);
}
