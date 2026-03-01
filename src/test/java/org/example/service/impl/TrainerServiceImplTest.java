package org.example.service.impl;

import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock TrainerRepository trainerRepository;
    @Mock TrainingRepository trainingRepository;
    @Mock TrainingTypeRepository trainingTypeRepository;
    @Mock UserService userService;

    @InjectMocks TrainerServiceImpl trainerService;

    private User user;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Jane.Smith");
        user.setPassword("pass456");
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
    void create_success() {
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.of(trainingType));
        when(userService.createUser("Jane", "Smith")).thenReturn(user);
        when(trainerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);

        assertThat(result.getUser().getUsername()).isEqualTo("Jane.Smith");
        assertThat(result.getTrainingType().getTrainingTypeName()).isEqualTo(TrainingType.TrainingTypeName.CARDIO);
        verify(trainerRepository).save(any());
    }

    @Test
    void create_trainingTypeNotFound_throwsException() {
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TrainingType not found");
    }

    @Test
    void matchUsernameAndPassword_correct_returnsTrue() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        assertThat(trainerService.matchUsernameAndPassword("Jane.Smith", "pass456")).isTrue();
    }

    @Test
    void matchUsernameAndPassword_wrong_returnsFalse() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "wrong")).thenReturn(false);
        assertThat(trainerService.matchUsernameAndPassword("Jane.Smith", "wrong")).isFalse();
    }

    @Test
    void findByUsername_success() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.findByUsername("Jane.Smith", "pass456");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("Jane.Smith");
    }

    @Test
    void findByUsername_invalidCredentials_throwsException() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> trainerService.findByUsername("Jane.Smith", "wrong"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void changePassword_success() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        trainerService.changePassword("Jane.Smith", "pass456", "newPass999");

        assertThat(user.getPassword()).isEqualTo("newPass999");
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changePassword_invalidOldPassword_throwsException() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> trainerService.changePassword("Jane.Smith", "wrong", "newPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void update_success() {
        TrainingType strength = new TrainingType();
        strength.setTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH);

        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH))
                .thenReturn(Optional.of(strength));
        when(trainerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.update("Jane.Smith", "pass456", TrainingType.TrainingTypeName.STRENGTH, true);

        assertThat(result.getTrainingType().getTrainingTypeName()).isEqualTo(TrainingType.TrainingTypeName.STRENGTH);
        assertThat(result.getUser().isActive()).isTrue();
    }

    @Test
    void update_trainingTypeNotFound_throwsException() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.STRENGTH))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.update("Jane.Smith", "pass456", TrainingType.TrainingTypeName.STRENGTH, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TrainingType not found");
    }

    @Test
    void setActive_deactivate_success() {
        user.setActive(true);
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        trainerService.setActive("Jane.Smith", "pass456", false);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void setActive_alreadyActive_throwsException() {
        user.setActive(true);
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertThatThrownBy(() -> trainerService.setActive("Jane.Smith", "pass456", true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already active");
    }

    @Test
    void setActive_alreadyInactive_throwsException() {
        user.setActive(false);
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        assertThatThrownBy(() -> trainerService.setActive("Jane.Smith", "pass456", false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already inactive");
    }

    @Test
    void getTrainings_success() {
        Training training = new Training();
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "pass456")).thenReturn(true);
        when(trainingRepository.findByTrainerUserUsername("Jane.Smith")).thenReturn(List.of(training));

        List<Training> result = trainerService.getTrainings("Jane.Smith", "pass456", null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTrainings_invalidCredentials_throwsException() {
        when(trainerRepository.existsByUserUsernameAndUserPassword("Jane.Smith", "wrong")).thenReturn(false);

        assertThatThrownBy(() -> trainerService.getTrainings("Jane.Smith", "wrong", null, null, null))
                .isInstanceOf(SecurityException.class);
    }
}