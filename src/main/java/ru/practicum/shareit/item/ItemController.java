package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.api.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) {
        log.info("Пришел Get запрос /items/{id} с id: {}", id);
        ItemDto itemDto = itemService.getById(id);
        log.info("Отправлен ответ Get /items/{id} с телом: {}", itemDto);
        return itemDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ItemDto create(@RequestBody @Valid ItemCreateDto itemCreateDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Пришел Post запрос /items с телом: {} и заголовком X-Sharer-User-Id: {} ", itemCreateDto, ownerId);
        ItemDto itemDto = itemService.create(itemCreateDto, ownerId);
        log.info("Отправлен ответ Post /items с телом: {}", itemDto);
        return itemDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ItemDto update(@RequestBody @Valid ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id) {
        log.info("Пришел Patch запрос /items/{id} с телом: {}, заголовком X-Sharer-User-Id: {} и id: {}", itemUpdateDto, ownerId, id);
        ItemDto itemDto = itemService.update(itemUpdateDto, ownerId, id);
        log.info("Отправлен ответ Patch /items/{id} с телом: {}", itemDto);
        return itemDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ItemOwnerDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Пришел Get запрос /items с заголовком X-Sharer-User-Id: {} " +
                "на получение списка вещей пользователя", ownerId);
        List<ItemOwnerDto> items = itemService.getByOwner(ownerId);
        log.info("Отправлен ответ Get /items с телом: {} ", items);
        return items;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    List<ItemDto> search(@RequestParam String text) {
        log.info("Пришел Get запрос /items на поиск с телом: {}", text);
        List<ItemDto> items = itemService.search(text);
        log.info("Отправлен ответ Get /items с результатом поиска: {} ", items);
        return items;
    }

}
