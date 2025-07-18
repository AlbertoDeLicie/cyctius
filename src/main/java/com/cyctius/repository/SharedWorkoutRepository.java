package com.cyctius.repository;

import com.cyctius.entity.SharedWorkout;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface SharedWorkoutRepository extends CrudRepository<SharedWorkout, String> {

    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}
