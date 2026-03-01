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
