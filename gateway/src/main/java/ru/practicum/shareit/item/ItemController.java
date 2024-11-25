package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("Пришел Get запрос /items/{id} с id: {}", id);
        return itemClient.getById(id);
    }

    @PostMapping
    ResponseEntity<Object> create(@RequestBody @Valid ItemCreateDto itemCreateDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Пришел Post запрос /items с телом: {} и заголовком X-Sharer-User-Id: {} ", itemCreateDto, ownerId);
        return itemClient.create(itemCreateDto, ownerId);
    }

    @PatchMapping("/{id}")
    ResponseEntity<Object> update(@RequestBody @Valid ItemUpdateDto itemUpdateDto, @RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id) {
        log.info("Пришел Patch запрос /items/{id} с телом: {}, заголовком X-Sharer-User-Id: {} и id: {}", itemUpdateDto, ownerId, id);
        return itemClient.update(itemUpdateDto, ownerId, id);
    }

    @GetMapping
    ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Пришел Get запрос /items с заголовком X-Sharer-User-Id: {} " +
                "на получение списка вещей пользователя", ownerId);
        return itemClient.getByOwner(ownerId);
    }

    @GetMapping("/search")
    ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Пришел Get запрос /items на поиск с телом: {}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    ResponseEntity<Object> addComment(@RequestBody CreateCommentDto createCommentDto, @PathVariable long itemId,
                                      @RequestHeader("X-Sharer-User-Id") long authorId) {
        log.info("Пришел Post запрос /items/{itemId}/comment с телом: {}, заголовком X-Sharer-User-Id: {} " +
                "и itemId: {}", createCommentDto, authorId, itemId);
        return itemClient.addComment(createCommentDto, itemId, authorId);
    }

}
