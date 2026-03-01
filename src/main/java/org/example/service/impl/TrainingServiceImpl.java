package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Override
    public Training create(String traineeUsername, String trainerUsername,
                           String name, TrainingType.TrainingTypeName typeName,
                           Date date, int durationMinutes) {
        validateNotBlank(traineeUsername, "Trainee username");
        validateNotBlank(trainerUsername, "Trainer username");
        validateNotBlank(name, "Training name");
        validateNotNull(typeName, "Training type");
        validateNotNull(date, "Training date");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername));

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(typeName)
                .orElseThrow(() -> new IllegalArgumentException("TrainingType not found: " + typeName));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(name);
        training.setTrainingType(trainingType);
        training.setDate(date);
        training.setNumber(durationMinutes);

        log.info("Creating training '{}' for trainee: {}, trainer: {}", name, traineeUsername, trainerUsername);
        return trainingRepository.save(training);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Training> select(Long id) {
        return trainingRepository.findById(id);
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
