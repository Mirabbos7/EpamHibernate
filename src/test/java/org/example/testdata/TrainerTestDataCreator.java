package org.example.testdata;

import lombok.RequiredArgsConstructor;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.repository.TrainerRepository;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TrainerTestDataCreator {

    private final TrainerRepository trainerRepository;
    private final UserTestDataCreator userTestDataCreator;
    private final TrainingTypeTestDataCreator trainingTypeTestDataCreator;

    public Trainer givenTrainerExists(Consumer<Trainer> config) {
        final var entity = new Trainer();

        // Not-null fields
        User user = userTestDataCreator.givenUserExists(u -> {});
        entity.setUser(user);

        TrainingType trainingType = trainingTypeTestDataCreator.givenTrainingTypeExists(
                tt -> tt.setTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH)
        );
        entity.setTrainingType(trainingType);

        config.accept(entity);
        return trainerRepository.save(entity);
    }
}

