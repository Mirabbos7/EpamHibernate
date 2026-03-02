package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO:
    //  1) A good practice here and in other entities is to explicitly specify fetch type
    //  2) Let's initialize collections to avoid NPE
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
