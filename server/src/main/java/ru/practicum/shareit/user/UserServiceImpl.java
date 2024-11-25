package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DuplicateException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.api.JpaUserRepository;
import ru.practicum.shareit.user.api.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return UserMapper.toUserDto(findById(id));
    }

    @Override
    public UserDto create(UserCreateDto userDto) {
        checkEmailUsed(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserUpdateDto userDto, long id) {
        User existed = findById(id);
        String dtoEmail = userDto.getEmail();
        if (userDto.getName() != null) {
            existed.setName(userDto.getName());
        }
        if (dtoEmail != null) {
            if (!existed.getEmail().equals(dtoEmail)) {
                checkEmailUsed(dtoEmail);
                existed.setEmail(dtoEmail);
            }
        }
        userRepository.save(existed);
        return UserMapper.toUserDto(existed);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь c id {} не найден", id);
            return new NotFoundException(String.format("Пользователь c id %d не найден", id));
        });
    }

    private void checkEmailUsed(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("Данный email уже используется: {}", email);
            throw new DuplicateException("Данный email уже используется");
        }
    }

}
