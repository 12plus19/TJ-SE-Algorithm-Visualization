package com.blog.wang.algorithmgrade.service;

import com.blog.wang.algorithmgrade.pojo.UserRating;
import com.blog.wang.algorithmgrade.pojo.UserRatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRatingRepository extends JpaRepository<UserRating, UserRatingId> {
    UserRating findByUserIdAndAlgorithmId(String userId, String algorithmId);

    List<UserRating> findByAlgorithmId(String algorithmId);
}
