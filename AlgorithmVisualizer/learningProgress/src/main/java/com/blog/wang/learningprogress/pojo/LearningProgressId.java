package com.blog.wang.learningprogress.pojo;

import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Embeddable
public class LearningProgressId implements Serializable {
    private String userId;
    private String algorithmId;

    // 默认构造函数
    public LearningProgressId() {}

    public LearningProgressId(String userId, String algorithmId) {
        this.userId = userId;
        this.algorithmId = algorithmId;
    }

    // Getters 和 Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }

    // hashCode 和 equals 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningProgressId)) return false;
        LearningProgressId that = (LearningProgressId) o;
        return userId.equals(that.userId) && algorithmId.equals(that.algorithmId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + algorithmId.hashCode();
    }
}