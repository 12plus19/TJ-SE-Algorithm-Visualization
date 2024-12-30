package com.blog.wang.algorithm.pojo;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "algorithms")
public class Algorithm {

    @Id
    @Column(name="algorithmId")
    private String algorithmId;

    @Column(name="algorithmName",nullable = false)
    private String algorithmName;

    @Lob // 使用 @Lob 注解,表示长文本
    @Column(nullable = false)
    private String description;

    @Column(name="difficultyLevel",nullable = false)
    private int difficultyLevel;

    //
    // 在构造函数中生成唯一 ID
    public Algorithm() {
        this.algorithmId = UUID.randomUUID().toString(); // 生成唯一的字符串 ID
    }

    public String getAlgorithmId() {
        return algorithmId;
    }
    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }
    public String getAlgorithmName() {
        return algorithmName;
    }
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getDifficultyLevel() {
        return difficultyLevel;
    }
    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
