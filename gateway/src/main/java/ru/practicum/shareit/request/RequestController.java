package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestCreateDto itemRequest,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Post запрос /requests с телом: {} и заголовком X-Sharer-User-Id: {} ", itemRequest, userId);
        return requestClient.create(itemRequest, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Get запрос /requests с заголовком X-Sharer-User-Id: {} ", userId);
        return requestClient.getByRequestor(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Пришел Get запрос /requests/all с заголовком X-Sharer-User-Id: {} ", userId);
        return requestClient.getAll(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("Пришел Get запрос /requests/id c id = {}", id);
        return requestClient.getById(id);
    }
}