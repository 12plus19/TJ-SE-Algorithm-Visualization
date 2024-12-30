package com.blog.wang.algorithmgrade.pojo;
import java.io.Serializable;
import jakarta.persistence.Id;

public class UserRatingId implements Serializable {
    @Id
    private String userId;

    @Id
    private String algorithmId;

    // 默认构造函数
    public UserRatingId() {}

    public UserRatingId(String userId, String algorithmId) {
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

    // 重写 equals() 和 hashCode() 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRatingId)) return false;
        UserRatingId that = (UserRatingId) o;
        return userId.equals(that.userId) && algorithmId.equals(that.algorithmId);
    }

    @Override
    public int hashCode() {
        return 31 * userId.hashCode() + algorithmId.hashCode();
    }
}