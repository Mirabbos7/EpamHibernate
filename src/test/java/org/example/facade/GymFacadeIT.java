package org.example.facade;

import org.example.AbstractSpringIntegrationTest;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.entity.TrainingType.TrainingTypeName.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;

class GymFacadeIT extends AbstractSpringIntegrationTest {

    @Autowired
    GymFacade gymFacade;
    @MockitoSpyBean
    AuthService authenticator;

    @BeforeEach
    void setUp() {
        doNothing().when(authenticator).authenticate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("1. Should create a Trainer profile and persist it with generated username and password")
    void shouldCreateTrainerProfile() {
        trainingTypeCreator.givenTrainingTypeExists(tt -> tt.setTrainingTypeName(CARDIO));

        final var trainer = gymFacade.createTrainer("Alice", "Smith", CARDIO);

        assertThat(trainer.getId()).isNotNull();
        assertThat(trainer.getUser().getUsername()).isNotBlank();
        assertThat(trainer.getUser().getPassword()).isNotBlank();
        assertThat(trainer.getUser().getFirstName()).isEqualTo("Alice");
        assertThat(trainer.getUser().getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("2. Should create a Trainee profile and persist it with generated username and password")
    void shouldCreateTraineeProfile() {
        final var trainee = gymFacade.createTrainee("Bob", "Brown", new Date(), "42 Oak Ave");

        assertThat(trainee.getId()).isNotNull();
        assertThat(trainee.getUser().getUsername()).isNotBlank();
        assertThat(trainee.getUser().getPassword()).isNotBlank();
        assertThat(trainee.getUser().getFirstName()).isEqualTo("Bob");
        assertThat(trainee.getAddress()).isEqualTo("42 Oak Ave");
    }

    @Nested
    @DisplayName("3. Trainee credential matching")
    class TraineeCredentialMatching {

        @Test
        @DisplayName("Should return true when trainee username and password match")
        void shouldReturnTrueWhenTraineeCredentialsMatch() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            assertThat(gymFacade.matchTraineeCredentials(username, password)).isTrue();
        }

        @Test
        @DisplayName("Should return false when trainee password is wrong")
        void shouldReturnFalseWhenTraineePasswordIsWrong() {
            final var trainee = traineeCreator.givenTraineeExists();

            assertThat(gymFacade.matchTraineeCredentials(trainee.getUser().getUsername(), "wrong-password")).isFalse();
        }
    }

    @Nested
    @DisplayName("4. Trainer credential matching")
    class TrainerCredentialMatching {

        @Test
        @DisplayName("Should return true when trainer username and password match")
        void shouldReturnTrueWhenTrainerCredentialsMatch() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            assertThat(gymFacade.matchTrainerCredentials(username, password)).isTrue();
        }

        @Test
        @DisplayName("Should return false when trainer password is wrong")
        void shouldReturnFalseWhenTrainerPasswordIsWrong() {
            final var trainer = trainerCreator.givenTrainerExists();

            assertThat(gymFacade.matchTrainerCredentials(trainer.getUser().getUsername(), "wrong-password")).isFalse();
        }
    }

    @Nested
    @DisplayName("5. Select Trainer profile by username")
    class SelectTrainerByUsername {

        @Test
        @DisplayName("Should return trainer when valid credentials are provided")
        void shouldReturnTrainerForValidCredentials() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            final var result = gymFacade.getTrainer(username, password);

            assertThat(result).isPresent();
            assertThat(result.get().getUser().getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("Should return empty Optional when trainer username does not exist")
        void shouldReturnEmptyWhenTrainerNotFound() {
            final var result = gymFacade.getTrainer("ghost.trainer", "any");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. Select Trainee profile by username")
    class SelectTraineeByUsername {

        @Test
        @DisplayName("Should return trainee when valid credentials are provided")
        void shouldReturnTraineeForValidCredentials() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var result = gymFacade.getTrainee(username, password);

            assertThat(result).isPresent();
            assertThat(result.get().getUser().getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("Should return empty Optional when trainee username does not exist")
        void shouldReturnEmptyWhenTraineeNotFound() {
            final var result = gymFacade.getTrainee("ghost.trainee", "any");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("7. Trainee password change")
    class TraineePasswordChange {

        @Test
        @DisplayName("Should successfully change trainee password when old password is correct")
        void shouldChangeTraineePasswordSuccessfully() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var oldPassword = trainee.getUser().getPassword();

            gymFacade.changeTraineePassword(username, oldPassword, "newSecurePass");

            assertThat(gymFacade.matchTraineeCredentials(username, "newSecurePass")).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when old trainee password is incorrect")
        void shouldThrowWhenOldTraineePasswordIsIncorrect() {
            doCallRealMethod().when(authenticator).authenticate(anyString(), anyString(), any());
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();

            assertThatThrownBy(() -> gymFacade.changeTraineePassword(username, "wrongOld", "newPass"))
                    .isInstanceOf(SecurityException.class);
        }
    }

    @Nested
    @DisplayName("8. Trainer password change")
    class TrainerPasswordChange {

        @Test
        @DisplayName("Should successfully change trainer password when old password is correct")
        void shouldChangeTrainerPasswordSuccessfully() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var username = trainer.getUser().getUsername();
            final var oldPassword = trainer.getUser().getPassword();

            gymFacade.changeTrainerPassword(username, oldPassword, "newSecurePass");

            assertThat(gymFacade.matchTrainerCredentials(username, "newSecurePass")).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when old trainer password is incorrect")
        void shouldThrowWhenOldTrainerPasswordIsIncorrect() {
            doCallRealMethod().when(authenticator).authenticate(anyString(), anyString(), any());
            final var trainer = trainerCreator.givenTrainerExists();
            final var username = trainer.getUser().getUsername();

            assertThatThrownBy(() -> gymFacade.changeTrainerPassword(username, "wrongOld", "newPass"))
                    .isInstanceOf(SecurityException.class);
        }
    }

    @Nested
    @DisplayName("9. Update Trainer profile")
    class UpdateTrainerProfile {

        @Test
        @DisplayName("Should update trainer specialization and active status")
        void shouldUpdateTrainerProfile() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();
            trainingTypeCreator.givenTrainingTypeExists(tt -> tt.setTrainingTypeName(FLEXIBILITY));

            final var updated = gymFacade.updateTrainer(username, password, FLEXIBILITY, false);

            assertThat(updated.getTrainingType().getTrainingTypeName()).isEqualTo(FLEXIBILITY);
            assertThat(updated.getUser().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("10. Update Trainee profile")
    class UpdateTraineeProfile {

        @Test
        @DisplayName("Should update trainee date of birth and address")
        void shouldUpdateTraineeProfile() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();
            final var newDob = new Date(0);

            final var updated = gymFacade.updateTrainee(username, password, newDob, "New Address", false);

            assertThat(updated.getAddress()).isEqualTo("New Address");
            assertThat(updated.getDateOfBirth()).isEqualTo(newDob);
            assertThat(updated.getUser().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("11. Activate/De-activate Trainee")
    class ToggleTraineeActive {

        @Test
        @DisplayName("Should activate an inactive trainee")
        void shouldActivateTrainee() {
            final var trainee = traineeCreator.givenTraineeExists(t -> t.getUser().setActive(false));
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            gymFacade.setTraineeActive(username, password, true);

            final var result = gymFacade.getTrainee(username, password);
            assertThat(result).isPresent();
            assertThat(result.get().getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should de-activate an active trainee")
        void shouldDeactivateTrainee() {
            final var trainee = traineeCreator.givenTraineeExists(t -> t.getUser().setActive(true));
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            gymFacade.setTraineeActive(username, password, false);

            final var result = gymFacade.getTrainee(username, password);
            assertThat(result).isPresent();
            assertThat(result.get().getUser().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("12. Activate/De-activate Trainer")
    class ToggleTrainerActive {

        @Test
        @DisplayName("Should activate an inactive trainer")
        void shouldActivateTrainer() {
            final var trainer = trainerCreator.givenTrainerExists(t -> t.getUser().setActive(false));
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            gymFacade.setTrainerActive(username, password, true);

            final var result = gymFacade.getTrainer(username, password);
            assertThat(result).isPresent();
            assertThat(result.get().getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should de-activate an active trainer")
        void shouldDeactivateTrainer() {
            final var trainer = trainerCreator.givenTrainerExists(t -> t.getUser().setActive(true));
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            gymFacade.setTrainerActive(username, password, false);

            final var result = gymFacade.getTrainer(username, password);
            assertThat(result).isPresent();
            assertThat(result.get().getUser().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("13. Delete Trainee profile by username")
    class DeleteTraineeProfile {

        @Test
        @DisplayName("Should delete trainee and all associated trainings when credentials are valid")
        void shouldDeleteTraineeByUsername() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            gymFacade.deleteTrainee(username, password);

            assertThat(gymFacade.getTrainee(username, password)).isEmpty();
            assertThat(traineeCreator.getTraineeRepository().findById(trainee.getId())).isEmpty();

        }

        @Test
        @DisplayName("Should cascade delete all trainee's trainings when trainee is deleted")
        void shouldCascadeDeleteTraineeTrainings() {
            final var trainee = withDbSync(() -> {
                final var it = traineeCreator.givenTraineeExists();
                final var trainer = trainerCreator.givenTrainerExists();
                trainingCreator.givenTrainingExists(t -> {
                    t.setTrainee(it);
                    t.setTrainer(trainer);
                });
                trainingCreator.givenTrainingExists(t -> {
                    t.setTrainee(it);
                    t.setTrainer(trainer);
                });
                return it;
            });

            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();
            withDbSync(() -> gymFacade.deleteTrainee(username, password));

            assertThat(traineeCreator.getTraineeRepository().findById(trainee.getId())).isEmpty();
            assertThat(trainingCreator.getTrainingRepository().findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("14. Get Trainee Trainings List by criteria")
    class GetTraineeTrainings {

        @Test
        @DisplayName("Should return all trainings for a trainee when no filter criteria are applied")
        void shouldReturnAllTraineeTrainingsWithoutFilters() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
            });
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
            });
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var trainings = gymFacade.getTraineeTrainings(username, password, null, null,
                    null, null);

            assertThat(trainings).hasSize(2);
        }

        @Test
        @DisplayName("Should return trainings between dates")
        void shouldFilterTraineeTrainingsByDateRange() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            final var past = new Date(1000L);
            final var recent = new Date();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
                t.setDate(past);
            });
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
                t.setDate(recent);
            });
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var trainings = gymFacade.getTraineeTrainings(
                    username,
                    password,
                    new Date(past.getTime() - 1000L), // just before the past training
                    new Date(recent.getTime() + 1000L), // just after the recent training
                    null,
                    null);

            assertThat(trainings).hasSize(2);
        }

        @Test
        @DisplayName("Should return trainings filtered by trainer name")
        void shouldFilterTraineeTrainingsByTrainerName() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
            });
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();
            final var trainerUsername = trainer.getUser().getUsername();

            final var trainings = gymFacade.getTraineeTrainings(username, password, null, null,
                    trainerUsername, null);

            assertThat(trainings).isNotEmpty();
        }

        @Test
        @DisplayName("Should return trainings filtered by training type")
        void shouldFilterTraineeTrainingsByTrainingType() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainingType = trainingTypeCreator.givenTrainingTypeExists(
                    t -> t.setTrainingTypeName(STRENGTH)
            );
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
                t.setTrainingType(trainingType);
            });

            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var trainings = gymFacade.getTraineeTrainings(username, password, null, null,
                    null, STRENGTH);

            assertThat(trainings).isNotEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no trainings match the given type")
        void shouldReturnEmptyListWhenNoTraineeTrainingsMatchCriteria() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainingType = trainingTypeCreator.givenTrainingTypeExists(
                    t -> t.setTrainingTypeName(STRENGTH)
            );
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
                t.setTrainingType(trainingType);
            });
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var trainings = gymFacade.getTraineeTrainings(username, password, null, null,
                    null, CARDIO);

            assertThat(trainings).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no trainings match the given date range")
        void shouldReturnEmptyListWhenNoTraineeTrainingsMatchDateRange() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainee(trainee);
                t.setTrainer(trainer);
                t.setDate(new Date(1000L));
            });
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var trainings = gymFacade.getTraineeTrainings(
                    username,
                    password,
                    new Date(2000L), // after the training date
                    new Date(3000L), // after the training date
                    null,
                    null);

            assertThat(trainings).isEmpty();
        }
    }

    @Nested
    @DisplayName("15. Get Trainer Trainings List by criteria")
    class GetTrainerTrainings {

        @Test
        @DisplayName("Should return all trainings for a trainer when no filter criteria are applied")
        void shouldReturnAllTrainerTrainingsWithoutFilters() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
            });
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
            });
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            final var trainings = gymFacade.getTrainerTrainings(username, password, null, null, null);

            assertThat(trainings).hasSize(2);
        }

        @Test
        @DisplayName("Should return trainings filtered by from-date and to-date")
        void shouldFilterTrainerTrainingsByDateRange() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
                t.setDate(new Date(1000L));
            });
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
                t.setDate(new Date());
            });
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();

            final var trainings = gymFacade.getTrainerTrainings(username, password, null, null, null);

            assertThat(trainings).hasSize(2);
        }

        @Test
        @DisplayName("Should return trainings filtered by trainee name")
        void shouldFilterTrainerTrainingsByTraineeName() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
            });
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();
            final var traineeUsername = trainee.getUser().getUsername();

            final var trainings = gymFacade.getTrainerTrainings(username, password, null, null, traineeUsername);

            assertThat(trainings).isNotEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no trainings match the given criteria")
        void shouldReturnEmptyListWhenNoTrainerTrainingsMatchCriteria() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists();
            trainingCreator.givenTrainingExists(t -> {
                t.setTrainer(trainer);
                t.setTrainee(trainee);
                t.setDate(new Date(1000L));
            });
            final var username = trainer.getUser().getUsername();
            final var password = trainer.getUser().getPassword();
            final var trainings = gymFacade
                    .getTrainerTrainings(username, password, null, null, UUID.randomUUID().toString());

            assertThat(trainings).isEmpty();
        }
    }

    @Nested
    @DisplayName("16. Add Training")
    class AddTraining {

        @Test
        @DisplayName("Should persist a new training linked to the given trainee and trainer")
        void shouldAddTrainingSuccessfully() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer = trainerCreator.givenTrainerExists();
            trainingTypeCreator.givenTrainingTypeExists(tt -> tt.setTrainingTypeName(BALANCE));
            final var traineeUsername = trainee.getUser().getUsername();
            final var trainerUsername = trainer.getUser().getUsername();

            final var training = gymFacade.createTraining(traineeUsername, trainerUsername,
                    "Morning Session", BALANCE, new Date(), 45);

            assertThat(training.getId()).isNotNull();
            assertThat(training.getTrainee().getUser().getUsername()).isEqualTo(traineeUsername);
            assertThat(training.getTrainer().getUser().getUsername()).isEqualTo(trainerUsername);
        }

        @Test
        @DisplayName("Should throw exception when trainee username does not exist")
        void shouldThrowWhenTraineeNotFoundForTraining() {
            final var trainer = trainerCreator.givenTrainerExists();
            trainingTypeCreator.givenTrainingTypeExists(tt -> tt.setTrainingTypeName(BALANCE));

            assertThatThrownBy(() -> gymFacade.createTraining("no.such.trainee",
                    trainer.getUser().getUsername(), "Session", BALANCE, new Date(), 30))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should throw exception when trainer username does not exist")
        void shouldThrowWhenTrainerNotFoundForTraining() {
            final var trainee = traineeCreator.givenTraineeExists();
            trainingTypeCreator.givenTrainingTypeExists(tt -> tt.setTrainingTypeName(BALANCE));

            assertThatThrownBy(() -> gymFacade.createTraining(trainee.getUser().getUsername(),
                    "no.such.trainer", "Session", BALANCE, new Date(), 30))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("17. Get trainers list not assigned to a trainee")
    class GetUnassignedTrainers {

        @Test
        @DisplayName("Should return all active trainers when trainee has no assigned trainers")
        void shouldReturnAllTrainersWhenNoneAssigned() {
            final var trainee = traineeCreator.givenTraineeExists();
            trainerCreator.givenTrainerExists();
            trainerCreator.givenTrainerExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var unassigned = gymFacade.getUnassignedTrainers(username, password);

            assertThat(unassigned).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Should exclude already-assigned trainers from the result")
        void shouldExcludeAlreadyAssignedTrainers() {
            final var assigned = trainerCreator.givenTrainerExists();
            final var unassignedTrainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists(t -> t.setTrainers(List.of(assigned)));
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var result = gymFacade.getUnassignedTrainers(username, password);

            final var resultUsernames = result.stream().map(tr -> tr.getUser().getUsername()).toList();
            assertThat(resultUsernames).doesNotContain(assigned.getUser().getUsername());
            assertThat(resultUsernames).contains(unassignedTrainer.getUser().getUsername());
        }

        @Test
        @DisplayName("Should return empty list when all trainers are already assigned to the trainee")
        void shouldReturnEmptyListWhenAllTrainersAreAssigned() {
            final var trainer1 = trainerCreator.givenTrainerExists();
            final var trainer2 = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists(t -> t.setTrainers(List.of(trainer1, trainer2)));
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var result = gymFacade.getUnassignedTrainers(username, password);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("18. Update Trainee's trainers list")
    class UpdateTraineeTrainers {

        @Test
        @DisplayName("Should replace trainee's trainer list with the provided trainer usernames")
        void shouldUpdateTraineeTrainersList() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var trainer1 = trainerCreator.givenTrainerExists();
            final var trainer2 = trainerCreator.givenTrainerExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var updated = gymFacade.updateTraineeTrainers(username, password,
                    List.of(trainer1.getUser().getUsername(), trainer2.getUser().getUsername()));

            final var trainerUsernames = updated.getTrainers().stream().map(t -> t.getUser().getUsername()).toList();
            assertThat(trainerUsernames).containsExactlyInAnyOrder(
                    trainer1.getUser().getUsername(), trainer2.getUser().getUsername());
        }

        @Test
        @DisplayName("Should result in an empty trainer list when an empty list is provided")
        void shouldClearTraineeTrainersListWhenEmptyListProvided() {
            final var trainer = trainerCreator.givenTrainerExists();
            final var trainee = traineeCreator.givenTraineeExists(t -> t.setTrainers(List.of(trainer)));
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            final var updated = gymFacade.updateTraineeTrainers(username, password, List.of());

            assertThat(updated.getTrainers()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when a provided trainer username does not exist")
        void shouldThrowWhenTrainerUsernameNotFound() {
            final var trainee = traineeCreator.givenTraineeExists();
            final var username = trainee.getUser().getUsername();
            final var password = trainee.getUser().getPassword();

            assertThatThrownBy(() -> gymFacade.updateTraineeTrainers(username, password, List.of("no.such.trainer")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
