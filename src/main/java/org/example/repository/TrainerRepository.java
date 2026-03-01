package org.example.repository;

import org.example.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT COUNT(t) > 0 FROM Trainer t WHERE t.user.username = :username AND t.user.password = :password")
    boolean existsByUserUsernameAndUserPassword(@Param("username") String username,
                                                @Param("password") String password);

    @Query("""
            SELECT tr FROM Trainer tr
            WHERE tr NOT IN (
                SELECT t FROM Trainee tn JOIN tn.trainers t
                WHERE tn.user.username = :traineeUsername
            )
            """)
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}