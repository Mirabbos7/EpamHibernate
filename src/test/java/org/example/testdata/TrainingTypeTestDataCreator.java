package org.example.testdata;

import lombok.RequiredArgsConstructor;
import org.example.entity.TrainingType;
import org.example.repository.TrainingTypeRepository;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TrainingTypeTestDataCreator {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingType givenTrainingTypeExists(Consumer<TrainingType> config) {
        final var entity = new TrainingType();

        // Not-null fields
        entity.setTrainingTypeName(TrainingType.TrainingTypeName.CARDIO);

        config.accept(entity);
        return trainingTypeRepository.save(entity);
    }
}

