package org.example.service.impl;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock TraineeRepository traineeRepository;
    @Mock TrainerRepository trainerRepository;
    @Mock TrainingRepository trainingRepository;
    @Mock UserService userService;

    @InjectMocks TraineeServiceImpl traineeService;

    private User user;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("John.Doe");
        user.setPassword("pass123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new ArrayList<>());
    }

    @Test
    void create_success() {
        when(userService.createUser("John", "Doe")).thenReturn(user);
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainee result = traineeService.create("John", "Doe", new Date(), "123 Main St");

        assertThat(result.getUser().getUsername()).isEqualTo("John.Doe");
        assertThat(result.getAddress()).isEqualTo("123 Main St");
        verify(traineeRepository).save(any());
    }

    @Test
    void matchUsernameAndPassword_correct_returnsTrue() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        assertThat(traineeService.matchUsernameAndPassword("John.Doe", "pass123")).isTrue();
    }

    @Test
    void matchUsernameAndPassword_wrong_returnsFalse() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "wrong")).thenReturn(false);
        assertThat(traineeService.matchUsernameAndPassword("John.Doe", "wrong")).isFalse();
    }

    @Test
    void findByUsername_success() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findByUsername("John.Doe", "pass123");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("John.Doe");
    }

    @Test
    void findByUsername_invalidCredentials_throwsException() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> traineeService.findByUsername("John.Doe", "wrong"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void changePassword_success() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        traineeService.changePassword("John.Doe", "pass123", "newPass999");

        assertThat(user.getPassword()).isEqualTo("newPass999");
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changePassword_invalidOldPassword_throwsException() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> traineeService.changePassword("John.Doe", "wrong", "newPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void update_success() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainee result = traineeService.update("John.Doe", "pass123", new Date(), "New Address", true);

        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(result.getUser().isActive()).isTrue();
    }

    @Test
    void setActive_deactivate_success() {
        user.setActive(true);
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        traineeService.setActive("John.Doe", "pass123", false);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void setActive_alreadyActive_throwsException() {
        user.setActive(true);
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> traineeService.setActive("John.Doe", "pass123", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already active");
    }

    @Test
    void setActive_alreadyInactive_throwsException() {
        user.setActive(false);
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> traineeService.setActive("John.Doe", "pass123", false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already inactive");
    }

    @Test
    void delete_success() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));

        traineeService.delete("John.Doe", "pass123");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void delete_invalidCredentials_throwsException() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> traineeService.delete("John.Doe", "wrong"))
                .isInstanceOf(SecurityException.class);
        verify(traineeRepository, never()).delete(any());
    }

    @Test
    void getTrainings_success() {
        Training training = new Training();
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(trainingRepository.findByTraineeUserUsername("John.Doe")).thenReturn(List.of(training));

        List<Training> result = traineeService.getTrainings("John.Doe", "pass123", null, null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnassignedTrainers_success() {
        Trainer trainer = new Trainer();
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(trainerRepository.findTrainersNotAssignedToTrainee("John.Doe")).thenReturn(List.of(trainer));

        List<Trainer> result = traineeService.getUnassignedTrainers("John.Doe", "pass123");

        assertThat(result).hasSize(1);
    }

    @Test
    void updateTrainers_success() {
        User trainerUser = new User();
        trainerUser.setUsername("Jane.Smith");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainee result = traineeService.updateTrainers("John.Doe", "pass123", List.of("Jane.Smith"));

        assertThat(result.getTrainers()).hasSize(1);
        assertThat(result.getTrainers().get(0).getUser().getUsername()).isEqualTo("Jane.Smith");
    }

    @Test
    void updateTrainers_trainerNotFound_throwsException() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("John.Doe", "pass123")).thenReturn(true);
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> traineeService.updateTrainers("John.Doe", "pass123", List.of("unknown")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found: unknown");
    }
}