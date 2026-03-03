package org.example.service.impl;

import org.example.entity.*;
import org.example.repository.*;
import org.example.service.AuthService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock TraineeRepository traineeRepository;
    @Mock TrainerRepository trainerRepository;
    @Mock TrainingRepository trainingRepository;
    @Mock UserService userService;
    @Mock AuthService authService;

    @InjectMocks TraineeServiceImpl service;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john.doe");
        user.setPassword("pass123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress("123 Main St");
        trainee.setDateOfBirth(new Date());
    }

    @Test
    void create_shouldSaveAndReturnTrainee() {
        when(userService.createUser("John", "Doe")).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = service.create("John", "Doe", new Date(), "123 Main St");

        assertThat(result.getUser().getUsername()).isEqualTo("john.doe");
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void matchUsernameAndPassword_shouldReturnTrue() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("john.doe", "pass123")).thenReturn(true);
        assertThat(service.matchUsernameAndPassword("john.doe", "pass123")).isTrue();
    }

    @Test
    void matchUsernameAndPassword_shouldReturnFalse() {
        when(traineeRepository.existsByUserUsernameAndUserPassword("john.doe", "wrong")).thenReturn(false);
        assertThat(service.matchUsernameAndPassword("john.doe", "wrong")).isFalse();
    }

    @Test
    void findByUsername_shouldReturnTrainee() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThat(service.findByUsername("john.doe", "pass123")).isPresent();
    }

    @Test
    void findByUsername_shouldThrow_whenAuthFails() {
        doThrow(new SecurityException("Invalid credentials"))
                .when(authService).authenticate(eq("john.doe"), eq("wrong"), any());

        assertThatThrownBy(() -> service.findByUsername("john.doe", "wrong"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        service.changePassword("john.doe", "pass123", "newPass");

        assertThat(trainee.getUser().getPassword()).isEqualTo("newPass");
    }

    @Test
    void changePassword_shouldThrow_whenNotFound() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword("john.doe", "pass123", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee not found");
    }

    @Test
    void update_shouldUpdateFields() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = service.update("john.doe", "pass123", new Date(), "New Address", false);

        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(result.getUser().isActive()).isFalse();
    }

    @Test
    void setActive_shouldDeactivate() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        service.setActive("john.doe", "pass123", false);

        assertThat(trainee.getUser().isActive()).isFalse();
    }

    @Test
    void setActive_shouldThrow_whenAlreadySameState() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> service.setActive("john.doe", "pass123", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already active");
    }

    @Test
    void delete_shouldDeleteTrainee() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));

        service.delete("john.doe", "pass123");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("john.doe", "pass123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee not found");
    }

    @Test
    void getTrainings_shouldReturnList() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(trainingRepository.findByTraineeUserUsername("john.doe")).thenReturn(List.of(new Training()));

        assertThat(service.getTrainings("john.doe", "pass123", null, null, null, null)).hasSize(1);
    }

    @Test
    void getUnassignedTrainers_shouldReturnList() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByTraineesNotContaining(trainee)).thenReturn(List.of(new Trainer()));

        assertThat(service.getUnassignedTrainers("john.doe", "pass123")).hasSize(1);
    }

    @Test
    void updateTrainers_shouldAssignTrainers() {
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("jane.smith");
        trainer.setUser(trainerUser);

        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = service.updateTrainers("john.doe", "pass123", List.of("jane.smith"));

        assertThat(result.getTrainers()).hasSize(1);
    }

    @Test
    void updateTrainers_shouldThrow_whenTrainerNotFound() {
        doNothing().when(authService).authenticate(eq("john.doe"), eq("pass123"), any());
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainers("john.doe", "pass123", List.of("unknown")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }
}