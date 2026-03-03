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

    // TODO:
    //  Prefer derived Spring Data queries where possible (method-name queries).
    //  Let's trust in framework maturity as it can handle most of the scenarios and is more maintainable.
    //  Use @Query when the query cannot be expressed cleanly via method name
    //  (e.g., complex joins/subqueries/projections) or when you need explicit fetch tuning.
    //  Are trainer.getUser() / trainer.getTrainingType() called outside the transactional boundary?
    @Query("select t from Trainer t join fetch t.user join fetch t.trainingType where t.user.username = :username")
    Optional<Trainer> findByUserUsername(@Param("username") String username);

    // TODO:
    //  What was the reason to use @Query here?
    @Query("SELECT COUNT(t) > 0 FROM Trainer t WHERE t.user.username = :username AND t.user.password = :password")
    boolean existsByUserUsernameAndUserPassword(@Param("username") String username,
                                                @Param("password") String password);

    // TODO:
    //  Even this one can be done without @Query: findByTraineesNotContaining(Trainee trainee)
    @Query("""
            SELECT tr FROM Trainer tr
            WHERE tr NOT IN (
                SELECT t FROM Trainee tn JOIN tn.trainers t
                WHERE tn.user.username = :traineeUsername
            )
            """)
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}