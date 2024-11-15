package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.api.ItemRepository;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.api.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto getById(long id) {
        return ItemMapper.toItemDto(findById(id));
    }

    @Override
    public ItemDto create(ItemCreateDto itemCreateDto, long ownerId) {
        userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(ownerId);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(ItemUpdateDto itemUpdateDto, long ownerId, long id) {
        Item existed = findById(id);
        if (existed.getOwner() != ownerId) {
            log.error("Ошибка аутентификации: Владелец вещи c id = {}: {}, запрос от : {}",
                    id, existed.getOwner(), ownerId);
            throw new ForbiddenException("Ошибка аутентификации");
        }
        if (itemUpdateDto.getName() != null) {
            existed.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            existed.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            existed.setAvailable(itemUpdateDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(existed));
    }

    @Override
    public List<ItemOwnerDto> getByOwner(long ownerId) {
        return itemRepository.getByOwner(ownerId).stream()
                .map(ItemMapper::toItemOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item findById(long id) {
        return itemRepository.getById(id)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", id));
                });
    }
}
