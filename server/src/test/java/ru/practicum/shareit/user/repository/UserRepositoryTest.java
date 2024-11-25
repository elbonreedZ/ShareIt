package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.api.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private JpaUserRepository userRepository;

    @Test
    public void findByEmail() {
        User user =  new User();
        user.setName("user");
        user.setEmail("email@mail.com");
        user = userRepository.save(user);
        Optional<User> emailUser = userRepository.findByEmail(user.getEmail());
        assertTrue(emailUser.isPresent());
        assertEquals(emailUser.get(), user);
    }

    @Test
    public void cannotSaveWithDuplicateEmails() {
        User user =  new User();
        user.setName("user");
        user.setEmail("email@mail.com");
        User userDuplicate = new User();
        userDuplicate.setName("duplicate");
        userDuplicate.setEmail(user.getEmail());
        userRepository.save(user);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(userDuplicate));
    }
}
