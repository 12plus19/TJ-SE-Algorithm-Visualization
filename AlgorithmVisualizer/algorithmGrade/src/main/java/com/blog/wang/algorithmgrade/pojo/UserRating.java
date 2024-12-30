package com.blog.wang.algorithmgrade.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "userRatings")
@IdClass(UserRatingId.class) // 指定复合主键类
public class UserRating {

    @Id
    @Column(name="userId")
    private String userId;

    @Id
    @Column(name="algorithmId")
    private String algorithmId;

    @Column(nullable = false)
    private int rating; //0-5分

    //
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
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
