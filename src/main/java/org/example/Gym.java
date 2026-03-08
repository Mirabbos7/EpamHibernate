package org.example;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.facade.GymFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@ComponentScan("org.example")
@EnableJpaRepositories("org.example.repository")
public class Gym {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(Gym.class);
        GymFacade facade = ctx.getBean(GymFacade.class);

        System.out.println("\n===== CREATE TRAINEE =====");
        Trainee trainee = facade.createTrainee("John", "Doe", new Date(), "123 Main St");
        System.out.println("Created trainee: " + trainee.getUser().getUsername());
        System.out.println("Generated password: " + trainee.getUser().getPassword());

        String traineeUsername = trainee.getUser().getUsername();
        String traineePassword = trainee.getUser().getPassword();

        System.out.println("\n===== CREATE TRAINER =====");
        Trainer trainer = facade.createTrainer("Jane", "Smith", TrainingType.TrainingTypeName.CARDIO);
        System.out.println("Created trainer: " + trainer.getUser().getUsername());
        System.out.println("Generated password: " + trainer.getUser().getPassword());

        String trainerUsername = trainer.getUser().getUsername();
        String trainerPassword = trainer.getUser().getPassword();

        System.out.println("\n===== MATCH CREDENTIALS =====");
        boolean traineeMatch = facade.matchTraineeCredentials(traineeUsername, traineePassword);
        System.out.println("Trainee credentials match: " + traineeMatch);
        boolean trainerMatch = facade.matchTrainerCredentials(trainerUsername, trainerPassword);
        System.out.println("Trainer credentials match: " + trainerMatch);

        System.out.println("\n===== GET TRAINEE PROFILE =====");
        Optional<Trainee> fetchedTrainee = facade.getTrainee(traineeUsername, traineePassword);
        fetchedTrainee.ifPresent(t -> System.out.println("Fetched: " + t.getUser().getFirstName()
                + " " + t.getUser().getLastName() + ", address: " + t.getAddress()));

        System.out.println("\n===== GET TRAINER PROFILE =====");
        Optional<Trainer> fetchedTrainer = facade.getTrainer(trainerUsername, trainerPassword);
        fetchedTrainer.ifPresent(t -> System.out.println("Fetched: " + t.getUser().getFirstName()
                + " " + t.getUser().getLastName()
                + ", specialization: " + t.getTrainingType().getTrainingTypeName()));

        System.out.println("\n===== CHANGE PASSWORDS =====");
        String newTraineePassword = "newPass123";
        facade.changeTraineePassword(traineeUsername, traineePassword, newTraineePassword);
        traineePassword = newTraineePassword;
        System.out.println("Trainee password changed successfully");

        String newTrainerPassword = "trainerPass99";
        facade.changeTrainerPassword(trainerUsername, trainerPassword, newTrainerPassword);
        trainerPassword = newTrainerPassword;
        System.out.println("Trainer password changed successfully");

        System.out.println("\n===== UPDATE TRAINEE =====");
        Trainee updated = facade.updateTrainee(traineeUsername, traineePassword,
                new Date(), "456 New Ave", true);
        System.out.println("Updated address: " + updated.getAddress());

        System.out.println("\n===== UPDATE TRAINER =====");
        Trainer updatedTrainer = facade.updateTrainer(trainerUsername, trainerPassword,
                TrainingType.TrainingTypeName.STRENGTH, true);
        System.out.println("Updated specialization: " + updatedTrainer.getTrainingType().getTrainingTypeName());

        System.out.println("\n===== GET UNASSIGNED TRAINERS =====");
        List<Trainer> unassigned = facade.getUnassignedTrainers(traineeUsername, traineePassword);
        System.out.println("Unassigned trainers count: " + unassigned.size());

        System.out.println("\n===== UPDATE TRAINEE'S TRAINERS =====");
        Trainee withTrainers = facade.updateTraineeTrainers(traineeUsername, traineePassword,
                List.of(trainerUsername));
        System.out.println("Trainers assigned: " + withTrainers.getTrainers().size());

        System.out.println("\n===== CREATE TRAINING =====");
        Training training = facade.createTraining(traineeUsername, trainerUsername,
                "Morning Cardio", TrainingType.TrainingTypeName.STRENGTH,
                new Date(), 60);
        System.out.println("Created training: " + training.getName() + ", id=" + training.getId());

        System.out.println("\n===== GET TRAINING BY ID =====");
        Optional<Training> fetchedTraining = facade.getTraining(training.getId());
        fetchedTraining.ifPresent(t -> System.out.println("Fetched training: "
                + t.getName() + ", duration: " + t.getDurationInMinutes() + " min"));

        System.out.println("\n===== GET TRAINEE TRAININGS =====");
        List<Training> traineeTrainings = facade.getTraineeTrainings(
                traineeUsername, traineePassword, null, null, null, null);
        System.out.println("Trainee trainings count: " + traineeTrainings.size());

        System.out.println("\n===== GET TRAINER TRAININGS =====");
        List<Training> trainerTrainings = facade.getTrainerTrainings(
                trainerUsername, trainerPassword, null, null, null);
        System.out.println("Trainer trainings count: " + trainerTrainings.size());

        System.out.println("\n===== SET ACTIVE/INACTIVE =====");
        facade.setTraineeActive(traineeUsername, traineePassword, false);
        System.out.println("Trainee deactivated");
        facade.setTraineeActive(traineeUsername, traineePassword, true);
        System.out.println("Trainee reactivated");

        List<Training> nextWeekTrainings = facade.getTrainingsForTraineesNextWeek(
                List.of(trainee.getId())
        );
        System.out.println("Next week trainings count: " + nextWeekTrainings.size());

        System.out.println("\n===== DELETE TRAINEE =====");
        facade.deleteTrainee(traineeUsername, traineePassword);
        System.out.println("Trainee deleted: " + traineeUsername);

        System.out.println("\n===== ALL TESTS PASSED =====");
        ctx.close();
    }
}