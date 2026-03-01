package org.example.service;

import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer create(String firstName, String lastName, TrainingType.TrainingTypeName specialization);

    boolean matchUsernameAndPassword(String username, String password);

    Optional<Trainer> findByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainer update(String username, String password,
                   TrainingType.TrainingTypeName specialization, boolean isActive);

    void setActive(String username, String password, boolean active);

    List<Training> getTrainings(String username, String password,
                                Date fromDate, Date toDate, String traineeName);
}