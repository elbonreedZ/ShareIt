package ru.practicum.shareit.user.inMemory;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(long id);

    User save(User user);

    User update(User user);

    void delete(long id);

    Optional<User> findByEmail(String email);
}
