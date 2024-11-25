package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.api.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestBody ItemRequestCreateDto itemRequest,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Post запрос /requests с телом: {} и заголовком X-Sharer-User-Id: {} ", itemRequest, userId);
        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequest, userId);
        log.info("Отправлен ответ Post /requests с телом: {}", itemRequestDto);
        return itemRequestDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestWithItemsDto> getByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Get запрос /requests с заголовком X-Sharer-User-Id: {} ", userId);
        List<ItemRequestWithItemsDto> requestDtos = itemRequestService.getByRequestor(userId);
        log.info("Отправлен ответ Get /requests с телом: {}", requestDtos);
        return requestDtos;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Get запрос /requests/all с заголовком X-Sharer-User-Id: {} ", userId);
        List<ItemRequestDto> requestDtos = itemRequestService.getAll(userId);
        log.info("Отправлен ответ Get /requests с телом: {}", requestDtos);
        return requestDtos;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestWithItemsDto getById(@PathVariable long id) {
        log.info("Пришел Get запрос /requests/id c id = {}", id);
        ItemRequestWithItemsDto itemRequestDto = itemRequestService.getById(id);
        log.info("Отправлен ответ Get /requests/id с телом: {}", itemRequestDto);
        return itemRequestDto;
    }
}
