package com.cyctius.repository;

import com.cyctius.entity.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface WorkoutRepository extends PagingAndSortingRepository<Workout, String>, CrudRepository<Workout, String> {

    /**
     * Find all workouts by author ID.
     *
     * @param authorId the ID of the author
     * @return a list of workouts
     */
    List<Workout> findAllByAuthorId(String authorId);

    /**
     * Find all workouts by author ID with pagination.
     *
     * @param authorId the ID of the author
     * @param pageable the pagination information
     * @return a page of workouts
     */
    Page<Workout> findAllByAuthorId(String authorId, Pageable pageable);
}
