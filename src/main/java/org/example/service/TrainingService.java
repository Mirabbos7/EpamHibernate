package org.example.service;

import org.example.entity.Training;
import org.example.entity.TrainingType;

import java.util.Date;
import java.util.Optional;

public interface TrainingService {

    Training create(String traineeUsername, String trainerUsername,
                    String name, TrainingType.TrainingTypeName typeName,
                    Date date, int durationMinutes);

    Optional<Training> select(Long id);
}