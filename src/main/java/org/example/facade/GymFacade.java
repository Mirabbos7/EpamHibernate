package org.example.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        log.info("Facade: createTrainee [{} {}]", firstName, lastName);
        return traineeService.create(firstName, lastName, dateOfBirth, address);
    }

    public boolean matchTraineeCredentials(String username, String password) {
        log.info("Facade: matchTraineeCredentials [{}]", username);
        return traineeService.matchUsernameAndPassword(username, password);
    }

    public Optional<Trainee> getTrainee(String username, String password) {
        log.info("Facade: getTrainee [{}]", username);
        return traineeService.findByUsername(username, password);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changeTraineePassword [{}]", username);
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee updateTrainee(String username, String password, Date dateOfBirth, String address, boolean isActive) {
        log.info("Facade: updateTrainee [{}]", username);
        return traineeService.update(username, password, dateOfBirth, address, isActive);
    }

    public void setTraineeActive(String username, String password, boolean active) {
        log.info("Facade: setTraineeActive [{}, active={}]", username, active);
        traineeService.setActive(username, password, active);
    }

    public void deleteTrainee(String username, String password) {
        log.info("Facade: deleteTrainee [{}]", username);
        traineeService.delete(username, password);
    }

    public List<Training> getTraineeTrainings(String username, String password,
                                              Date fromDate, Date toDate,
                                              String trainerName,
                                              TrainingType.TrainingTypeName trainingTypeName) {
        log.info("Facade: getTraineeTrainings [{}]", username);
        return traineeService.getTrainings(username, password, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Trainer> getUnassignedTrainers(String username, String password) {
        log.info("Facade: getUnassignedTrainers [{}]", username);
        return traineeService.getUnassignedTrainers(username, password);
    }

    public Trainee updateTraineeTrainers(String username, String password, List<String> trainerUsernames) {
        log.info("Facade: updateTraineeTrainers [{}]", username);
        return traineeService.updateTrainers(username, password, trainerUsernames);
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType.TrainingTypeName specialization) {
        log.info("Facade: createTrainer [{} {}]", firstName, lastName);
        return trainerService.create(firstName, lastName, specialization);
    }

    public boolean matchTrainerCredentials(String username, String password) {
        log.info("Facade: matchTrainerCredentials [{}]", username);
        return trainerService.matchUsernameAndPassword(username, password);
    }

    public Optional<Trainer> getTrainer(String username, String password) {
        log.info("Facade: getTrainer [{}]", username);
        return trainerService.findByUsername(username, password);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changeTrainerPassword [{}]", username);
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainer updateTrainer(String username, String password,
                                 TrainingType.TrainingTypeName specialization, boolean isActive) {
        log.info("Facade: updateTrainer [{}]", username);
        return trainerService.update(username, password, specialization, isActive);
    }

    public void setTrainerActive(String username, String password, boolean active) {
        log.info("Facade: setTrainerActive [{}, active={}]", username, active);
        trainerService.setActive(username, password, active);
    }

    public List<Training> getTrainerTrainings(String username, String password,
                                              Date fromDate, Date toDate, String traineeName) {
        log.info("Facade: getTrainerTrainings [{}]", username);
        return trainerService.getTrainings(username, password, fromDate, toDate, traineeName);
    }

    public Training createTraining(String traineeUsername, String trainerUsername,
                                   String name, TrainingType.TrainingTypeName typeName,
                                   Date date, int durationMinutes) {
        log.info("Facade: createTraining [trainee={}, trainer={}]", traineeUsername, trainerUsername);
        return trainingService.create(traineeUsername, trainerUsername, name, typeName, date, durationMinutes);
    }

    public Optional<Training> getTraining(Long id) {
        log.info("Facade: getTraining [id={}]", id);
        return trainingService.select(id);
    }
}