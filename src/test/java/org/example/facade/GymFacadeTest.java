package org.example.facade;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
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
class GymFacadeTest {

    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;

    @InjectMocks GymFacade gymFacade;

    @Test
    void createTrainee_delegatesToService() {
        Trainee trainee = new Trainee();
        Date dob = new Date();
        when(traineeService.create("John", "Doe", dob, "123 St")).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee("John", "Doe", dob, "123 St");

        assertThat(result).isEqualTo(trainee);
        verify(traineeService).create("John", "Doe", dob, "123 St");
    }

    @Test
    void matchTraineeCredentials_delegatesToService() {
        when(traineeService.matchUsernameAndPassword("John.Doe", "pass")).thenReturn(true);

        boolean result = gymFacade.matchTraineeCredentials("John.Doe", "pass");

        assertThat(result).isTrue();
        verify(traineeService).matchUsernameAndPassword("John.Doe", "pass");
    }

    @Test
    void getTrainee_delegatesToService() {
        Trainee trainee = new Trainee();
        when(traineeService.findByUsername("John.Doe", "pass")).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = gymFacade.getTrainee("John.Doe", "pass");

        assertThat(result).contains(trainee);
        verify(traineeService).findByUsername("John.Doe", "pass");
    }

    @Test
    void changeTraineePassword_delegatesToService() {
        gymFacade.changeTraineePassword("John.Doe", "old", "new");

        verify(traineeService).changePassword("John.Doe", "old", "new");
    }

    @Test
    void updateTrainee_delegatesToService() {
        Trainee trainee = new Trainee();
        Date dob = new Date();
        when(traineeService.update("John.Doe", "pass", dob, "New St", true)).thenReturn(trainee);

        Trainee result = gymFacade.updateTrainee("John.Doe", "pass", dob, "New St", true);

        assertThat(result).isEqualTo(trainee);
        verify(traineeService).update("John.Doe", "pass", dob, "New St", true);
    }

    @Test
    void setTraineeActive_delegatesToService() {
        gymFacade.setTraineeActive("John.Doe", "pass", false);

        verify(traineeService).setActive("John.Doe", "pass", false);
    }

    @Test
    void deleteTrainee_delegatesToService() {
        gymFacade.deleteTrainee("John.Doe", "pass");

        verify(traineeService).delete("John.Doe", "pass");
    }

    @Test
    void getTraineeTrainings_delegatesToService() {
        Date from = new Date();
        Date to = new Date();
        List<Training> trainings = List.of(new Training());
        when(traineeService.getTrainings("John.Doe", "pass", from, to, "Jane", TrainingType.TrainingTypeName.CARDIO))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings(
                "John.Doe", "pass", from, to, "Jane", TrainingType.TrainingTypeName.CARDIO);

        assertThat(result).isEqualTo(trainings);
        verify(traineeService).getTrainings("John.Doe", "pass", from, to, "Jane", TrainingType.TrainingTypeName.CARDIO);
    }

    @Test
    void getUnassignedTrainers_delegatesToService() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.getUnassignedTrainers("John.Doe", "pass")).thenReturn(trainers);

        List<Trainer> result = gymFacade.getUnassignedTrainers("John.Doe", "pass");

        assertThat(result).isEqualTo(trainers);
        verify(traineeService).getUnassignedTrainers("John.Doe", "pass");
    }

    @Test
    void updateTraineeTrainers_delegatesToService() {
        Trainee trainee = new Trainee();
        List<String> usernames = List.of("Jane.Smith");
        when(traineeService.updateTrainers("John.Doe", "pass", usernames)).thenReturn(trainee);

        Trainee result = gymFacade.updateTraineeTrainers("John.Doe", "pass", usernames);

        assertThat(result).isEqualTo(trainee);
        verify(traineeService).updateTrainers("John.Doe", "pass", usernames);
    }

    @Test
    void createTrainer_delegatesToService() {
        Trainer trainer = new Trainer();
        when(trainerService.create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);

        assertThat(result).isEqualTo(trainer);
        verify(trainerService).create("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);
    }

    @Test
    void matchTrainerCredentials_delegatesToService() {
        when(trainerService.matchUsernameAndPassword("Jane.Smith", "pass")).thenReturn(true);

        boolean result = gymFacade.matchTrainerCredentials("Jane.Smith", "pass");

        assertThat(result).isTrue();
        verify(trainerService).matchUsernameAndPassword("Jane.Smith", "pass");
    }

    @Test
    void getTrainer_delegatesToService() {
        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("Jane.Smith", "pass")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = gymFacade.getTrainer("Jane.Smith", "pass");

        assertThat(result).contains(trainer);
        verify(trainerService).findByUsername("Jane.Smith", "pass");
    }

    @Test
    void changeTrainerPassword_delegatesToService() {
        gymFacade.changeTrainerPassword("Jane.Smith", "old", "new");

        verify(trainerService).changePassword("Jane.Smith", "old", "new");
    }

    @Test
    void updateTrainer_delegatesToService() {
        Trainer trainer = new Trainer();
        when(trainerService.update("Jane.Smith", "pass", TrainingType.TrainingTypeName.STRENGTH, true))
                .thenReturn(trainer);

        Trainer result = gymFacade.updateTrainer("Jane.Smith", "pass", TrainingType.TrainingTypeName.STRENGTH, true);

        assertThat(result).isEqualTo(trainer);
        verify(trainerService).update("Jane.Smith", "pass", TrainingType.TrainingTypeName.STRENGTH, true);
    }

    @Test
    void setTrainerActive_delegatesToService() {
        gymFacade.setTrainerActive("Jane.Smith", "pass", false);

        verify(trainerService).setActive("Jane.Smith", "pass", false);
    }

    @Test
    void getTrainerTrainings_delegatesToService() {
        Date from = new Date();
        Date to = new Date();
        List<Training> trainings = List.of(new Training());
        when(trainerService.getTrainings("Jane.Smith", "pass", from, to, "John"))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("Jane.Smith", "pass", from, to, "John");

        assertThat(result).isEqualTo(trainings);
        verify(trainerService).getTrainings("Jane.Smith", "pass", from, to, "John");
    }

    @Test
    void createTraining_delegatesToService() {
        Training training = new Training();
        Date date = new Date();
        when(trainingService.create("John.Doe", "Jane.Smith", "Run",
                TrainingType.TrainingTypeName.CARDIO, date, 60)).thenReturn(training);

        Training result = gymFacade.createTraining(
                "John.Doe", "Jane.Smith", "Run", TrainingType.TrainingTypeName.CARDIO, date, 60);

        assertThat(result).isEqualTo(training);
        verify(trainingService).create("John.Doe", "Jane.Smith", "Run",
                TrainingType.TrainingTypeName.CARDIO, date, 60);
    }

    @Test
    void getTraining_delegatesToService() {
        Training training = new Training();
        when(trainingService.select(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = gymFacade.getTraining(1L);

        assertThat(result).contains(training);
        verify(trainingService).select(1L);
    }

    @Test
    void getTraining_notFound_returnsEmpty() {
        when(trainingService.select(99L)).thenReturn(Optional.empty());

        Optional<Training> result = gymFacade.getTraining(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void createTrainee_doesNotCallTrainerOrTrainingService() {
        when(traineeService.create(any(), any(), any(), any())).thenReturn(new Trainee());

        gymFacade.createTrainee("John", "Doe", new Date(), "123 St");

        verifyNoInteractions(trainerService, trainingService);
    }

    @Test
    void createTrainer_doesNotCallTraineeOrTrainingService() {
        when(trainerService.create(any(), any(), any())).thenReturn(new Trainer());

        gymFacade.createTrainer("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);

        verifyNoInteractions(traineeService, trainingService);
    }
}