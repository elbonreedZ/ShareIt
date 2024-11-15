package ru.practicum.shareit.item.api;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto getById(long id);

    ItemDto create(ItemCreateDto itemCreateDto, long ownerId);

    ItemDto update(ItemUpdateDto itemUpdateDto, long ownerId, long id);

    List<ItemOwnerDto> getByOwner(long ownerId);

    List<ItemDto> search(String text);
}
