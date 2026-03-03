package org.example.testdata;

import lombok.RequiredArgsConstructor;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.repository.TrainingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TrainingTestDataCreator {

    private final TrainingRepository trainingRepository;
    private final TrainerTestDataCreator trainerTestDataCreator;
    private final TraineeTestDataCreator traineeTestDataCreator;
    private final TrainingTypeTestDataCreator trainingTypeTestDataCreator;

    public Training givenTrainingExists() {
        return givenTrainingExists(t -> {});
    }

    public Training givenTrainingExists(Consumer<Training> config) {
        final var entity = new Training();

        // Not-null fields
        entity.setName("Training " + UUID.randomUUID().toString().substring(0, 8));
        entity.setDate(new Date());
        entity.setDurationInMinutes(60);

        // Relationships pre-populated for convenience
        final var trainer = trainerTestDataCreator.givenTrainerExists();
        entity.setTrainer(trainer);

        final var trainee = traineeTestDataCreator.givenTraineeExists();
        entity.setTrainee(trainee);

        final var trainingType = trainingTypeTestDataCreator.givenTrainingTypeExists(
                tt -> tt.setTrainingTypeName(TrainingType.TrainingTypeName.CARDIO)
        );
        entity.setTrainingType(trainingType);

        config.accept(entity);
        return trainingRepository.save(entity);
    }
}
