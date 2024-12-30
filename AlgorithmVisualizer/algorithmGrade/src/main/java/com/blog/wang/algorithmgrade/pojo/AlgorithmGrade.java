package com.blog.wang.algorithmgrade.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "algorithmGrades")
public class AlgorithmGrade {

    @Id
    @Column(name="algorithmId")
    private String algorithmId;

    @Column(nullable = false)
    private float grade;

    public String getAlgorithmId() {
        return algorithmId;
    }
    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }
    public float getGrade() {
        return grade;
    }
    public void setGrade(float grade) {
        this.grade = grade;
    }
}
