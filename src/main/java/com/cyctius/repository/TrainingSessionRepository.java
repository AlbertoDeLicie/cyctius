package com.cyctius.repository;

import com.cyctius.entity.TrainingSession;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSessionRepository extends CrudRepository<TrainingSession, String> {
    
    /**
     * Find all training sessions for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of training sessions
     */
    List<TrainingSession> findAllByUserId(String userId);
    
    /**
     * Find all training sessions for a user within a date range.
     *
     * @param userId the ID of the user
     * @param fromDate the start date (inclusive)
     * @param toDate the end date (inclusive)
     * @return a list of training sessions
     */
    List<TrainingSession> findAllByUserIdAndCompletedAtBetween(
        String userId, 
        LocalDateTime fromDate, 
        LocalDateTime toDate
    );
    
    /**
     * Find all completed training sessions for a user within a date range.
     *
     * @param userId the ID of the user
     * @param fromDate the start date (inclusive)
     * @param toDate the end date (inclusive)
     * @param status the status filter (e.g., "COMPLETED")
     * @return a list of training sessions
     */
    List<TrainingSession> findAllByUserIdAndCompletedAtBetweenAndStatus(
        String userId, 
        LocalDateTime fromDate, 
        LocalDateTime toDate,
        String status
    );
}

