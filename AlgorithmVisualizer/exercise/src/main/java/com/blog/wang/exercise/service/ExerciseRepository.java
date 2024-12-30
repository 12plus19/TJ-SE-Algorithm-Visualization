package com.blog.wang.exercise.service;

import com.blog.wang.exercise.pojo.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {

    @Query(value = "SELECT * FROM exercises WHERE algorithmId = ?1 ORDER BY RAND() LIMIT 20", nativeQuery = true)
    List<Exercise> findRandomExercisesByAlgorithmId(String algorithmId);

    List<Exercise> findByAlgorithmId(String algorithmId);

    Exercise findByExerciseId(String exerciseId);
}
