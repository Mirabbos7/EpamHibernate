package org.example.service.impl;

import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.AuthService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    TrainerRepository trainerRepository;
    @Mock
    TrainingRepository trainingRepository;
    @Mock
    TrainingTypeRepository trainingTypeRepository;
    @Mock
    UserService userService;
    @Mock
    AuthService authService;

    @InjectMocks
    TrainerServiceImpl service;

    private Trainer trainer;
    private User user;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("jane.smith");
        user.setPassword("pass123");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setActive(true);

        trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingType.TrainingTypeName.CARDIO);

        trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(trainingType);
    }

    @Test
    void create_shouldSaveAndReturnTrainer() {
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.of(trainingType));
        when(userService.createUser("Jane", "Smith")).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Trainer result = service.create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);

        assertThat(result.getUser().getUsername()).isEqualTo("jane.smith");
        assertThat(result.getTrainingType().getTrainingTypeName()).isEqualTo(TrainingType.TrainingTypeName.CARDIO);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void create_shouldThrow_whenTrainingTypeNotFound() {
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TrainingType not found");
    }

    @Test
    void matchUsernameAndPassword_shouldReturnTrue() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("jane.smith", "pass123")).thenReturn(true);
        assertThat(service.matchUsernameAndPassword("jane.smith", "pass123")).isTrue();
    }

    @Test
    void matchUsernameAndPassword_shouldReturnFalse() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("jane.smith", "wrong")).thenReturn(false);
        assertThat(service.matchUsernameAndPassword("jane.smith", "wrong")).isFalse();
    }

    @Test
    void findByUsername_shouldReturnTrainer() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));

        assertThat(service.findByUsername("jane.smith", "pass123")).isPresent();
    }

    @Test
    void findByUsername_shouldThrow_whenAuthFails() {
        doThrow(new SecurityException("Invalid credentials"))
                .when(authService).authenticate(eq("jane.smith"), eq("wrong"), any());

        assertThatThrownBy(() -> service.findByUsername("jane.smith", "wrong"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        service.changePassword("jane.smith", "pass123", "newPass");

        assertThat(trainer.getUser().getPassword()).isEqualTo("newPass");
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changePassword_shouldThrow_whenNotFound() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword("jane.smith", "pass123", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }

    @Test
    void update_shouldUpdateSpecializationAndActiveStatus() {
        TrainingType strengthType = new TrainingType();
        strengthType.setTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH);

        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH))
                .thenReturn(Optional.of(strengthType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = service.update("jane.smith", "pass123", TrainingType.TrainingTypeName.STRENGTH, false);

        assertThat(result.getTrainingType().getTrainingTypeName()).isEqualTo(TrainingType.TrainingTypeName.STRENGTH);
        assertThat(result.getUser().isActive()).isFalse();
    }

    @Test
    void update_shouldThrow_whenTrainingTypeNotFound() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("jane.smith", "pass123", TrainingType.TrainingTypeName.STRENGTH, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TrainingType not found");
    }

    @Test
    void setActive_shouldDeactivate_whenCurrentlyActive() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        service.setActive("jane.smith", "pass123", false);

        assertThat(trainer.getUser().isActive()).isFalse();
    }

    @Test
    void setActive_shouldThrow_whenAlreadySameState() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));

        assertThatThrownBy(() -> service.setActive("jane.smith", "pass123", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already active");
    }

    @Test
    void getTrainings_shouldReturnList() {
        doNothing().when(authService).authenticate(eq("jane.smith"), eq("pass123"), any());
        when(trainingRepository.findByTrainerUsernameAndTraineeUsernameAndDateBetween(
                eq("jane.smith"), any(), any(), any()
        )).thenReturn(List.of(new Training()));

        List<Training> result = service.getTrainings("jane.smith", "pass123", null, null, null);

        assertThat(result).hasSize(1);
    }
}