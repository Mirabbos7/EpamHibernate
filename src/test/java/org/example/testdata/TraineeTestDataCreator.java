package org.example.testdata;

import lombok.RequiredArgsConstructor;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.repository.TraineeRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TraineeTestDataCreator {

    private final TraineeRepository traineeRepository;
    private final UserTestDataCreator userTestDataCreator;

    public Trainee givenTraineeExists() {
        return givenTraineeExists(t -> {});
    }

    public Trainee givenTraineeExists(Consumer<Trainee> config) {
        final var entity = new Trainee();

        // Not-null fields
        final var user = userTestDataCreator.givenUserExists();
        entity.setUser(user);

        // Optional fields pre-populated for convenience
        entity.setDateOfBirth(new Date());
        entity.setAddress("123 Default St");

        config.accept(entity);
        return traineeRepository.save(entity);
    }
}
