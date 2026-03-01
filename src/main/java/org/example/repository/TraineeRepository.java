package org.example.repository;

import org.example.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @Query("SELECT COUNT(t) > 0 FROM Trainee t WHERE t.user.username = :username AND t.user.password = :password")
    boolean existsByUserUsernameAndUserPassword(@Param("username") String username,
                                                @Param("password") String password);
}