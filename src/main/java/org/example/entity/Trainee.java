package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user", "trainers", "trainings"})
@EqualsAndHashCode(exclude = {"user", "trainers", "trainings"})
public class Trainee {

    // TODO:
    //  Each entity has an id, right? Can we somehow define it only once for all entities?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateOfBirth;
    private String address;

    // TODO:
    //  Let's be explicit in code about database constraints:
    //  1) Is this relation optional - in other words can Trainee exist without User?
    //  2) Can we protect the table from having multiple Trainees with the same User?
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers = new ArrayList<>();
}