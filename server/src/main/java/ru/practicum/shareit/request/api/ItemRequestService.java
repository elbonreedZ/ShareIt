package ru.practicum.shareit.request.api;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, long userId);

    List<ItemRequestWithItemsDto> getByRequestor(long userId);

    List<ItemRequestDto> getAll(long userId);

    ItemRequestWithItemsDto getById(long id);

    ItemRequest findById(long id);
}
