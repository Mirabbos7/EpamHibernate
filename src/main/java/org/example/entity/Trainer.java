package org.example.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings;

    @ManyToOne
    @JoinColumn(name = "specialization")
    private TrainingType trainingType;

    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees;
}
