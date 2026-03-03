package org.example.facade;

import org.example.AbstractSpringIntegrationTest;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    // -------------------------------------------------------------------------
    // 1. Create Trainer profile
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("1. Should create a Trainer profile and persist it with generated username and password")
    void shouldCreateTrainerProfile() {
        // TODO: implement
    }

    // -------------------------------------------------------------------------
    // 2. Create Trainee profile
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("2. Should create a Trainee profile and persist it with generated username and password")
    void shouldCreateTraineeProfile() {
        // TODO: implement
    }

    // -------------------------------------------------------------------------
    // 3. Trainee username and password matching
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("3. Trainee credential matching")
    class TraineeCredentialMatching {

        @Test
        @DisplayName("Should return true when trainee username and password match")
        void shouldReturnTrueWhenTraineeCredentialsMatch() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return false when trainee password is wrong")
        void shouldReturnFalseWhenTraineePasswordIsWrong() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 4. Trainer username and password matching
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("4. Trainer credential matching")
    class TrainerCredentialMatching {

        @Test
        @DisplayName("Should return true when trainer username and password match")
        void shouldReturnTrueWhenTrainerCredentialsMatch() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return false when trainer password is wrong")
        void shouldReturnFalseWhenTrainerPasswordIsWrong() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 5. Select Trainer profile by username
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("5. Select Trainer profile by username")
    class SelectTrainerByUsername {

        @Test
        @DisplayName("Should return trainer when valid credentials are provided")
        void shouldReturnTrainerForValidCredentials() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return empty Optional when trainer username does not exist")
        void shouldReturnEmptyWhenTrainerNotFound() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 6. Select Trainee profile by username
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("6. Select Trainee profile by username")
    class SelectTraineeByUsername {

        @Test
        @DisplayName("Should return trainee when valid credentials are provided")
        void shouldReturnTraineeForValidCredentials() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return empty Optional when trainee username does not exist")
        void shouldReturnEmptyWhenTraineeNotFound() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 7. Trainee password change
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("7. Trainee password change")
    class TraineePasswordChange {

        @Test
        @DisplayName("Should successfully change trainee password when old password is correct")
        void shouldChangeTraineePasswordSuccessfully() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when old trainee password is incorrect")
        void shouldThrowWhenOldTraineePasswordIsIncorrect() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 8. Trainer password change
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("8. Trainer password change")
    class TrainerPasswordChange {

        @Test
        @DisplayName("Should successfully change trainer password when old password is correct")
        void shouldChangeTrainerPasswordSuccessfully() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when old trainer password is incorrect")
        void shouldThrowWhenOldTrainerPasswordIsIncorrect() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 9. Update Trainer profile
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("9. Update Trainer profile")
    class UpdateTrainerProfile {

        @Test
        @DisplayName("Should update trainer specialization and active status")
        void shouldUpdateTrainerProfile() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when updating trainer with wrong credentials")
        void shouldThrowWhenUpdatingTrainerWithWrongCredentials() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 10. Update Trainee profile
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("10. Update Trainee profile")
    class UpdateTraineeProfile {

        @Test
        @DisplayName("Should update trainee date of birth and address")
        void shouldUpdateTraineeProfile() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when updating trainee with wrong credentials")
        void shouldThrowWhenUpdatingTraineeWithWrongCredentials() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 11. Activate / De-activate Trainee
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("11. Activate/De-activate Trainee")
    class ToggleTraineeActive {

        @Test
        @DisplayName("Should activate an inactive trainee")
        void shouldActivateTrainee() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should de-activate an active trainee")
        void shouldDeactivateTrainee() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 12. Activate / De-activate Trainer
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("12. Activate/De-activate Trainer")
    class ToggleTrainerActive {

        @Test
        @DisplayName("Should activate an inactive trainer")
        void shouldActivateTrainer() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should de-activate an active trainer")
        void shouldDeactivateTrainer() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 13. Delete Trainee profile by username
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("13. Delete Trainee profile by username")
    class DeleteTraineeProfile {

        @Test
        @DisplayName("Should delete trainee and all associated trainings when credentials are valid")
        void shouldDeleteTraineeByUsername() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when deleting trainee with wrong credentials")
        void shouldThrowWhenDeletingTraineeWithWrongCredentials() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 14. Get Trainee Trainings List with criteria
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("14. Get Trainee Trainings List by criteria")
    class GetTraineeTrainings {

        @Test
        @DisplayName("Should return all trainings for a trainee when no filter criteria are applied")
        void shouldReturnAllTraineeTrainingsWithoutFilters() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return trainings filtered by from-date and to-date")
        void shouldFilterTraineeTrainingsByDateRange() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return trainings filtered by trainer name")
        void shouldFilterTraineeTrainingsByTrainerName() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return trainings filtered by training type")
        void shouldFilterTraineeTrainingsByTrainingType() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return empty list when no trainings match the given criteria")
        void shouldReturnEmptyListWhenNoTraineeTrainingsMatchCriteria() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 15. Get Trainer Trainings List with criteria
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("15. Get Trainer Trainings List by criteria")
    class GetTrainerTrainings {

        @Test
        @DisplayName("Should return all trainings for a trainer when no filter criteria are applied")
        void shouldReturnAllTrainerTrainingsWithoutFilters() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return trainings filtered by from-date and to-date")
        void shouldFilterTrainerTrainingsByDateRange() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return trainings filtered by trainee name")
        void shouldFilterTrainerTrainingsByTraineeName() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return empty list when no trainings match the given criteria")
        void shouldReturnEmptyListWhenNoTrainerTrainingsMatchCriteria() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 16. Add Training
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("16. Add Training")
    class AddTraining {

        @Test
        @DisplayName("Should persist a new training linked to the given trainee and trainer")
        void shouldAddTrainingSuccessfully() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when trainee username does not exist")
        void shouldThrowWhenTraineeNotFoundForTraining() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when trainer username does not exist")
        void shouldThrowWhenTrainerNotFoundForTraining() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 17. Get trainers not assigned to a trainee
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("17. Get trainers list not assigned to a trainee")
    class GetUnassignedTrainers {

        @Test
        @DisplayName("Should return all active trainers when trainee has no assigned trainers")
        void shouldReturnAllTrainersWhenNoneAssigned() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should exclude already-assigned trainers from the result")
        void shouldExcludeAlreadyAssignedTrainers() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should return empty list when all trainers are already assigned to the trainee")
        void shouldReturnEmptyListWhenAllTrainersAreAssigned() {
            // TODO: implement
        }
    }

    // -------------------------------------------------------------------------
    // 18. Update Trainee's trainers list
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("18. Update Trainee's trainers list")
    class UpdateTraineeTrainers {

        @Test
        @DisplayName("Should replace trainee's trainer list with the provided trainer usernames")
        void shouldUpdateTraineeTrainersList() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should result in an empty trainer list when an empty list is provided")
        void shouldClearTraineeTrainersListWhenEmptyListProvided() {
            // TODO: implement
        }

        @Test
        @DisplayName("Should throw exception when a provided trainer username does not exist")
        void shouldThrowWhenTrainerUsernameNotFound() {
            // TODO: implement
        }
    }
}
