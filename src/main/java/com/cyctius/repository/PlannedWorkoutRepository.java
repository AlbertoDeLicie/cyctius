package com.cyctius.repository;

import com.cyctius.entity.PlannedWorkout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PlannedWorkoutRepository extends JpaRepository<PlannedWorkout, String> {
    Page<PlannedWorkout> findAllByUserIdAndPlannedDateBetween(String userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
