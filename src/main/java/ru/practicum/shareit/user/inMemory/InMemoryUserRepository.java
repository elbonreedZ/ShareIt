package ru.practicum.shareit.user.inMemory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private long idCounter;
    private final Map<Long, User> usersByIds = new HashMap<>();
    private final Map<String, User> usersByEmails = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(usersByIds.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(usersByIds.get(id));
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        usersByEmails.put(user.getEmail(), user);
        usersByIds.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User old = usersByIds.get(user.getId());
        if (!old.getEmail().equals(user.getEmail())) {
            usersByEmails.remove(old.getEmail());
        }
        usersByEmails.put(user.getEmail(), user);
        usersByIds.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        User user = usersByIds.remove(id);
        usersByEmails.remove(user.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmails.get(email));
    }

    private long getNextId() {
        return ++idCounter;
    }
}
