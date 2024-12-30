package com.blog.wang.exercise.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @Column(name="exerciseId")
    private String exerciseId;

    @Column(name="algorithmId",nullable = false)
    private String algorithmId;

    @Column(nullable = false)
    private String question;

    @Column(name="correctAnswer",nullable = false)
    private String correctAnswer;

    @Column(nullable = false)
    private int score;

    //
    public Exercise() {
        this.exerciseId = UUID.randomUUID().toString(); // 生成唯一的字符串 ID
    }
    public String getExerciseId() {
        return exerciseId;
    }
    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }
    public String getAlgorithmId() {
        return algorithmId;
    }
    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}
