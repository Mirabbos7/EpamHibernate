package org.example.testdata;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class UserTestDataCreator {

    private final UserRepository userRepository;

    public User givenUserExists(Consumer<User> config) {
        final var entity = new User();

        // Not-null fields
        entity.setFirstName("First" + UUID.randomUUID().toString().substring(0, 6));
        entity.setLastName("Last" + UUID.randomUUID().toString().substring(0, 6));
        entity.setUsername("user." + UUID.randomUUID().toString().substring(0, 8));
        entity.setPassword(UUID.randomUUID().toString());
        entity.setActive(true);

        config.accept(entity);
        return userRepository.save(entity);
    }
}

