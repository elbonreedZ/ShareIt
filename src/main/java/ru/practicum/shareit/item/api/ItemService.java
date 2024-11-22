package ru.practicum.shareit.item.api;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemWIthCommentsDto getById(long id);

    ItemDto create(ItemCreateDto itemCreateDto, long ownerId);

    ItemDto update(ItemUpdateDto itemUpdateDto, long ownerId, long id);

    List<ItemWIthCommentsDto> getByOwner(long ownerId);

    List<ItemDto> search(String text);

    Item findById(long id);

    CommentDto addComment(CreateCommentDto createCommentDto, long itemId, long authorId);
}
