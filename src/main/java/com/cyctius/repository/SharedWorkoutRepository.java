package com.cyctius.repository;

import com.cyctius.entity.SharedWorkout;
import org.springframework.data.repository.CrudRepository;

public interface SharedWorkoutRepository extends CrudRepository<SharedWorkout, String> {
}
