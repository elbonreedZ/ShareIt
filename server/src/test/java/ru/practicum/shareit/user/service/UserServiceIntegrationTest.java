package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.DuplicateException;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    public void shouldCreateUserSuccessfully() {
        UserCreateDto userCreateDto = new UserCreateDto("najdmkd;ldm;l;", "amc;kmc;amc");
        UserDto expected = new UserDto(1, userCreateDto.getName(), userCreateDto.getEmail());
        UserDto actual = userService.create(userCreateDto);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    public void shouldThrowDuplicateExWhenCreateWithDuplicateEmail() {
        UserCreateDto user = new UserCreateDto("name", "email@mail.com");
        UserCreateDto userDuplicate = new UserCreateDto("name1", "email@mail.com");
        userService.create(user);
        assertThrows(DuplicateException.class, () -> userService.create(userDuplicate));
    }

    @Test
    public void shouldUpdateSuccessful() {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email");
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", "newEmail");

        UserDto created = userService.create(userCreateDto);
        UserDto actual = userService.update(userUpdateDto, created.getId());

        UserDto expected = new UserDto(created.getId(), userUpdateDto.getName(), userUpdateDto.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldThrowDuplicateExWhenUpdateWithDuplicateEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email");
        UserCreateDto userCreateDto2 = new UserCreateDto("name", "anotherEmail");
        UserUpdateDto userUpdateDuplicateEmail = new UserUpdateDto("newName", userCreateDto2.getEmail());

        UserDto created = userService.create(userCreateDto);
        userService.create(userCreateDto2);

        assertThrows(DuplicateException.class, () -> userService.update(userUpdateDuplicateEmail, created.getId()));
    }

}
