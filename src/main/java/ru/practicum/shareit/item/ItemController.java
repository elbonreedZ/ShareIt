package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) {
        return itemService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemDto create(@RequestBody @Valid ItemCreateDto itemCreateDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.create(itemCreateDto, ownerId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ItemDto update(@RequestBody @Valid ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id) {
        return itemService.update(itemUpdateDto, ownerId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ItemOwnerDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

}
