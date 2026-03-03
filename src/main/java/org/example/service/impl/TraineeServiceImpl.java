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
import org.example.service.AuthService;
import org.example.service.TraineeService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserService userService;
    private final AuthService authService;

    @Transactional
    @Override
    public Trainee create(String firstName, String lastName, Date dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setUser(userService.createUser(firstName, lastName));
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        log.info("Creating trainee: {}.{}", firstName, lastName);
        return traineeRepository.save(trainee);
    }

    @Override
    public boolean matchUsernameAndPassword(String username, String password) {
        return traineeRepository.existsByUserUsernameAndUserPassword(username, password);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Trainee> findByUsername(String username, String password) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        return traineeRepository.findByUserUsername(username);
    }

    @Transactional
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        authService.authenticate(username, oldPassword, this::matchUsernameAndPassword);
        Trainee trainee = getOrThrow(username);
        trainee.getUser().setPassword(newPassword);
        traineeRepository.save(trainee);
        log.info("Password changed: {}", username);
    }

    @Transactional
    @Override
    public Trainee update(String username, String password, Date dateOfBirth, String address, boolean isActive) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        Trainee trainee = getOrThrow(username);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.getUser().setActive(isActive);
        return traineeRepository.save(trainee);
    }

    @Transactional
    @Override
    public void setActive(String username, String password, boolean active) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        Trainee trainee = getOrThrow(username);
        if (trainee.getUser().isActive() == active) {
            throw new IllegalStateException("Already " + (active ? "active" : "inactive"));
        }
        trainee.getUser().setActive(active);
        traineeRepository.save(trainee);
    }

    @Transactional
    @Override
    public void delete(String username, String password) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        traineeRepository.delete(getOrThrow(username));
        log.info("Deleted trainee: {}", username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> getTrainings(String username, String password,
                                       Date fromDate, Date toDate,
                                       String trainerName,
                                       TrainingType.TrainingTypeName typeName) {

        authService.authenticate(username, password, this::matchUsernameAndPassword);

        List<Training> trainings =
                trainingRepository.findByTraineeUserUsername(username);

        return trainings.stream()
                .filter(t -> fromDate == null || !t.getDate().before(fromDate))
                .filter(t -> toDate == null || !t.getDate().after(toDate))
                .filter(t -> trainerName == null ||
                        t.getTrainer().getUser().getUsername().equals(trainerName))
                .filter(t -> typeName == null ||
                        t.getTrainingType().getTrainingTypeName().equals(typeName))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trainer> getUnassignedTrainers(String username, String password) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        Trainee trainee = getOrThrow(username);
        return trainerRepository.findByTraineesNotContaining(trainee);
    }

    @Transactional
    @Override
    public Trainee updateTrainers(String username, String password, List<String> trainerUsernames) {
        authService.authenticate(username, password, this::matchUsernameAndPassword);
        Trainee trainee = getOrThrow(username);
        List<Trainer> trainers = trainerUsernames.stream()
                .map(u -> trainerRepository.findByUserUsername(u)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + u)))
                .collect(Collectors.toList());
        trainee.setTrainers(trainers);
        return traineeRepository.save(trainee);
    }


    private Trainee getOrThrow(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
    }
}