package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest request;
}
