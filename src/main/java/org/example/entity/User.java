package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// TODO:
//  @ToString will generate a method that includes all fields of the class.
//  Is it safe to print password in console/logs?
//  Note: generated version can be found at /target/classes once you compile the file
@ToString

// TODO:
//  @EqualsAndHashCode should be used very carefully in JPA entities.
//  By default, Lombok generates equals() and hashCode() based on all fields of the class.
//  In your case, this includes:
//   - mutable fields (username, password, isActive)
//   - associations (@OneToOne)
//  This may lead to:
//   - unexpected lazy-loading of relationships
//   - issues when using entities inside Set / Map
//   - changes in hashCode after the entity is persisted
//  In the current form, it is safer to remove @EqualsAndHashCode from your entities
@EqualsAndHashCode
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // TODO:
    //  Try thinking of 'User' as an independent entity/business domain. It should be responsible only for a certain
    //  scope like profile information and authentication. Does it really need to know about Trainee and Trainer?
    //  What if later we'll need more types like Admin, Manager etc.?
    //  Unidirectional is simpler:
    //  - less code
    //  - less synchronization between entities
    //  - less risk of recursion in toString / equals etc.
    //  Sometimes we do need a bidirectional relationship but this is most likely not the case
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private Trainee traineeId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private Trainer trainerId;
}