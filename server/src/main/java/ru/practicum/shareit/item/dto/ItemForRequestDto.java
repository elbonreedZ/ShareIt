package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForRequestDto {
    long id;
    String name;
    long ownerId;
}
