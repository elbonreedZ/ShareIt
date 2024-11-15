package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Пришел Get запрос /users на получение всех пользователей");
        List<UserDto> users = userService.getAll();
        log.info("Отправлен ответ Get /users с телом: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Пришел Get запрос /users/{id} с id: {}", id);
        UserDto userDto = userService.getById(id);
        log.info("Отправлен ответ Get /users/{id} с телом: {}", userDto);
        return userDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserCreateDto user) {
        log.info("Пришел Post запрос /users с телом: {}", user);
        UserDto userDto = userService.create(user);
        log.info("Отправлен ответ Post /users с телом: {}", userDto);
        return userDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@RequestBody @Valid UserUpdateDto user, @PathVariable long id) {
        log.info("Пришел Patch запрос /users/{id} с телом: {} и id: {}", user, id);
        UserDto userDto = userService.update(user, id);
        log.info("Отправлен ответ Patch /users/{id} с телом: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Пришел Delete запрос /users/{id} с id: {}", id);
        userService.delete(id);
        log.info("Отправлен ответ Delete /users/{id} с id = {}: OK", id);
    }

}
