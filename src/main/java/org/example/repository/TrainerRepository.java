package org.example.repository;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t JOIN FETCH t.user JOIN FETCH t.trainingType WHERE t.user.username = :username")
    Optional<Trainer> findByUserUsername(@Param("username") String username);

    boolean existsByUserUsernameAndUserPassword(String username, String password);

    List<Trainer> findByTraineesNotContaining(Trainee trainee);
}