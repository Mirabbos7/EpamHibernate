package org.example.repository;

import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeUserUsername(String username);
    List<Training> findByTrainerUserUsername(String username);
}