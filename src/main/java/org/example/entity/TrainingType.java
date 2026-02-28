package org.example.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingTypeName trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings = new ArrayList<>();

    public enum TrainingTypeName {
        CARDIO,
        STRENGTH,
        FLEXIBILITY,
        BALANCE,
        OTHER
    }
}
