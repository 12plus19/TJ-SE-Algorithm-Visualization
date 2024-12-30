package com.blog.wang.exercise.controller;

import com.blog.wang.exercise.pojo.Exercise;
import com.blog.wang.exercise.service.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @GetMapping("/random/{algorithmId}")
    public ResponseEntity<?> getRandomExercises(@PathVariable String algorithmId) {
        List<Exercise> exercises = exerciseRepository.findByAlgorithmId(algorithmId);
        if (exercises.isEmpty()) {
            return new ResponseEntity<>("算法Id不正确", HttpStatus.NOT_FOUND);
        }
        List<Exercise> randomExercises = exerciseRepository.findRandomExercisesByAlgorithmId(algorithmId);
        return new ResponseEntity<>(randomExercises, HttpStatus.OK);
    }
    @PostMapping("/checkAnswer")
    public ResponseEntity<?> checkAnswer(@RequestParam String exerciseId, @RequestParam String answer) {
        Exercise exercise = exerciseRepository.findByExerciseId(exerciseId);
        if(exercise == null) {
            return new ResponseEntity<>("习题未找到", HttpStatus.NOT_FOUND);
        }
        int getScore = exercise.getCorrectAnswer().equals(answer) ? exercise.getScore() : 0;
        return new ResponseEntity<>(getScore, HttpStatus.OK);
    }

}
