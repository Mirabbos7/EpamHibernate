package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "training_type")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingTypeName trainingTypeName;

    // TODO:
    //  Bidirectional or unidirectional relationship trade-off
    //  1) Currently, there are no use-cases in the project where you call trainingType.getTrainings()
    //  2) To find all trainings of a certain type, you can add a method in TrainingRepository
    //  I recommend to start with simpler unidirectional relationship and only add the bidirectional one if you really need it.
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
