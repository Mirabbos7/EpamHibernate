package org.example.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean isActive;

    @OneToOne(mappedBy = "user")
    private Trainee traineeId;

    @OneToOne(mappedBy = "user")
    private Trainer trainerId;

}
