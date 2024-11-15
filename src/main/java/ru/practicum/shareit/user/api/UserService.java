package ru.practicum.shareit.user.api;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(long id);

    UserDto create(UserCreateDto user);

    UserDto update(UserUpdateDto user, long id);

    void delete(long id);

}
