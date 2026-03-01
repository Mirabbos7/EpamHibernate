package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainerService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Trainer create(String firstName, String lastName, TrainingType.TrainingTypeName specialization) {
        TrainingType type = trainingTypeRepository.findByTrainingTypeName(specialization)
                .orElseThrow(() -> new IllegalArgumentException("TrainingType not found: " + specialization));
        Trainer trainer = new Trainer();
        trainer.setUser(userService.createUser(firstName, lastName));
        trainer.setTrainingType(type);
        log.info("Creating trainer: {}.{}", firstName, lastName);
        return trainerRepository.save(trainer);
    }

    @Override
    public boolean matchUsernameAndPassword(String username, String password) {
        return trainerRepository.existsByUserUsernameAndUserPassword(username, password);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Trainer> findByUsername(String username, String password) {
        authenticate(username, password);
        return trainerRepository.findByUserUsername(username);
    }

    @Transactional
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        authenticate(username, oldPassword);
        Trainer trainer = getOrThrow(username);
        trainer.getUser().setPassword(newPassword);
        trainerRepository.save(trainer);
        log.info("Password changed: {}", username);
    }

    @Transactional
    @Override
    public Trainer update(String username, String password,
                          TrainingType.TrainingTypeName specialization, boolean isActive) {
        authenticate(username, password);
        Trainer trainer = getOrThrow(username);
        TrainingType type = trainingTypeRepository.findByTrainingTypeName(specialization)
                .orElseThrow(() -> new IllegalArgumentException("TrainingType not found: " + specialization));
        trainer.setTrainingType(type);
        trainer.getUser().setActive(isActive);
        return trainerRepository.save(trainer);
    }

    @Transactional
    @Override
    public void setActive(String username, String password, boolean active) {
        authenticate(username, password);
        Trainer trainer = getOrThrow(username);
        if (trainer.getUser().isActive() == active) {
            throw new IllegalStateException("Already " + (active ? "active" : "inactive"));
        }
        trainer.getUser().setActive(active);
        trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> getTrainings(String username, String password,
                                       Date fromDate, Date toDate, String traineeName) {
        authenticate(username, password);
        return trainingRepository.findByTrainerUserUsername(username);
    }

    private void authenticate(String username, String password) {
        if (!matchUsernameAndPassword(username, password)) {
            throw new SecurityException("Invalid credentials: " + username);
        }
    }

    private Trainer getOrThrow(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));
    }
}