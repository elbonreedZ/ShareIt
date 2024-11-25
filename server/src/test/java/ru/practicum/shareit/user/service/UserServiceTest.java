package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.DuplicateException;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.api.JpaUserRepository;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void shouldCreateSuccessful() {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email@mail.com");
        User user = new User(1, userCreateDto.getName(), userCreateDto.getEmail());
        UserDto expected = new UserDto(1, userCreateDto.getName(), userCreateDto.getEmail());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto created = userService.create(userCreateDto);
        assertEquals(expected, created);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldThrowDuplicateExWhenCreateWithDuplicateEmail() {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email@mail.com");
        when(userRepository.findByEmail(userCreateDto.getEmail()))
                .thenReturn(Optional.of(new User()));
        assertThrows(DuplicateException.class, () -> userService.create(userCreateDto));
    }

    @Test
    public void shouldUpdateSuccessful() {
        long id = 1;
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", "newEmail");
        User existed = new User(1, "name", "email");
        UserDto expected = new UserDto(1, userUpdateDto.getName(), userUpdateDto.getEmail());
        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));

        UserDto actual = userService.update(userUpdateDto, id);
        assertEquals(expected, actual);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldThrowDuplicateExWhenUpdateWithDuplicateEmail() {
        long id = 1;
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", "newEmail");
        User existed = new User(1, "name", "email");

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));
        when(userRepository.findByEmail(userUpdateDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(DuplicateException.class, () -> userService.update(userUpdateDto, id));
    }

    @Test
    public void shouldUpdateWithEmptyEmail() {
        long id = 1;
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", null);
        User existed = new User(1, "name", "email");
        UserDto expected = new UserDto(1, userUpdateDto.getName(), existed.getEmail());
        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));
        UserDto actual = userService.update(userUpdateDto, id);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldUpdateWithEmptyName() {
        long id = 1;
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, "newEmail");
        User existed = new User(1, "name", "email");
        UserDto expected = new UserDto(1, existed.getName(), userUpdateDto.getEmail());
        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));
        UserDto actual = userService.update(userUpdateDto, id);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeleteUserById() {
        long id = 1;
        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    public void shouldFindById() {
        long id = 1;
        User existed = new User(id, "name", "email");
        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));
        assertEquals(existed, userService.findById(id));
    }

    @Test
    public void shouldGetUserDtoById() {
        long id = 1;
        User existed = new User(id, "name", "email");
        UserDto expected = new UserDto(id, existed.getName(), existed.getEmail());
        when(userRepository.findById(id))
                .thenReturn(Optional.of(existed));
        assertEquals(expected, userService.getById(id));
    }

    @Test
    public void shouldFindAllUserDto() {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "name", "email"));
        users.add(new User(2, "name", "email"));
        List<UserDto> expected = new ArrayList<>();
        expected.add(new UserDto(1, "name", "email"));
        expected.add(new UserDto(2, "name", "email"));
        when(userRepository.findAll())
                .thenReturn(users);
        assertEquals(expected, userService.getAll());
    }
}

