package org.example.service;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TraineeService {

    Trainee create(String firstName, String lastName, Date dateOfBirth, String address);

    boolean matchUsernameAndPassword(String username, String password);

    Optional<Trainee> findByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    Trainee update(String username, String password, Date dateOfBirth, String address, boolean isActive);

    void setActive(String username, String password, boolean active);

    void delete(String username, String password);

    List<Training> getTrainings(String username, String password,
                                Date fromDate, Date toDate,
                                String trainerName, TrainingType.TrainingTypeName trainingTypeName);

    List<Trainer> getUnassignedTrainers(String username, String password);

    Trainee updateTrainers(String username, String password, List<String> trainerUsernames);
}