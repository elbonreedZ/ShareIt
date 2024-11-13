package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
}
