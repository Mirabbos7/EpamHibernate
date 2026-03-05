package org.example.repository;

import org.example.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeUserUsername(String username);

    @Query("""
                select t from Training t
                where (:trainerUsername is null or t.trainer.user.username = :trainerUsername)
                  and (:traineeUsername is null or t.trainee.user.username = :traineeUsername)
                  and (:fromDate is null or t.date >= :fromDate)
                  and (:toDate is null or t.date <= :toDate)
            """)
    List<Training> findByTrainerUsernameAndTraineeUsernameAndDateBetween(
            @Param("trainerUsername") String trainerUsername,
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );
}