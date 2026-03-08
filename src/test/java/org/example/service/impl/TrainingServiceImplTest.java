package org.example.service.impl;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock TrainingRepository trainingRepository;
    @Mock TraineeRepository traineeRepository;
    @Mock TrainerRepository trainerRepository;
    @Mock TrainingTypeRepository trainingTypeRepository;

    @InjectMocks TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setUsername("John.Doe");
        trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("Jane.Smith");
        trainer = new Trainer();
        trainer.setUser(trainerUser);

        trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingType.TrainingTypeName.CARDIO);
    }

    @Test
    void create_success() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Training result = trainingService.create(
                "John.Doe", "Jane.Smith", "Morning Run",
                TrainingType.TrainingTypeName.CARDIO, new Date(), 60);

        assertThat(result.getName()).isEqualTo("Morning Run");
        assertThat(result.getDurationInMinutes()).isEqualTo(60);
        assertThat(result.getTrainee()).isEqualTo(trainee);
        assertThat(result.getTrainer()).isEqualTo(trainer);
        verify(trainingRepository).save(any());
    }

    @Test
    void create_blankTraineeUsername_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee username is required");
    }

    @Test
    void create_blankTrainerUsername_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer username is required");
    }

    @Test
    void create_blankName_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Training name is required");
    }

    @Test
    void create_nullType_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "Run", null, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Training type is required");
    }

    @Test
    void create_nullDate_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, null, 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Training date is required");
    }

    @Test
    void create_zeroDuration_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Training duration must be positive");
    }

    @Test
    void create_negativeDuration_throwsException() {
        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), -10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Training duration must be positive");
    }

    @Test
    void create_traineeNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(
                "unknown", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee not found: unknown");
    }

    @Test
    void create_trainerNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "unknown", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found: unknown");
    }

    @Test
    void create_trainingTypeNotFound_throwsException() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(
                "John.Doe", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, new Date(), 60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TrainingType not found");
    }

    @Test
    void select_found() {
        Training training = new Training();
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.select(1L);

        assertThat(result).isPresent();
    }

    @Test
    void select_notFound() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.select(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(new Training()));

        List<Training> result = trainingService.getTraineeTrainings(
                "John.Doe", null, null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTraineeTrainings_blankUsername_throwsException() {
        assertThatThrownBy(() -> trainingService.getTraineeTrainings(
                "", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee username is required");
    }

    @Test
    void getTrainerTrainings_shouldReturnList() {
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(new Training()));

        List<Training> result = trainingService.getTrainerTrainings(
                "Jane.Smith", null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTrainerTrainings_blankUsername_throwsException() {
        assertThatThrownBy(() -> trainingService.getTrainerTrainings(
                "", null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer username is required");
    }

    @Test
    void getTrainingsForTraineesNextWeek_shouldReturnList() {
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(new Training()));

        List<Training> result = trainingService.getTrainingsForTraineesNextWeek(List.of(1L, 2L));

        assertThat(result).hasSize(1);
    }

    @Test
    void getTrainingsForTraineesNextWeek_emptyIds_throwsException() {
        assertThatThrownBy(() -> trainingService.getTrainingsForTraineesNextWeek(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee ids list cannot be empty");
    }

    @Test
    void getTrainingsForTraineesNextWeek_nullIds_throwsException() {
        assertThatThrownBy(() -> trainingService.getTrainingsForTraineesNextWeek(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee ids list cannot be empty");
    }
}