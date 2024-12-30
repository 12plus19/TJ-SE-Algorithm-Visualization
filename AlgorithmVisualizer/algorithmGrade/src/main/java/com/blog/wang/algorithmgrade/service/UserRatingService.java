package com.blog.wang.algorithmgrade.service;

import com.blog.wang.algorithmgrade.pojo.AlgorithmGrade;
import com.blog.wang.algorithmgrade.pojo.UserRating;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserRatingService {

    @Autowired
    private UserRatingRepository userRatingRepository;

    @Autowired
    private AlgorithmGradeRepository algorithmGradeRepository;

    public UserRating createRating(String userId, String algorithmId, int rating) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setAlgorithmId(algorithmId);
        userRating.setRating(rating);
        return userRatingRepository.save(userRating);
    }

    public UserRating updateRating(String userId, String algorithmId, int rating) {
        UserRating userRating = userRatingRepository.findByUserIdAndAlgorithmId(userId, algorithmId);
        if (userRating != null) {
            userRating.setRating(rating);
            return userRatingRepository.save(userRating);
        }
        return null; // 或者抛出异常
    }

    public Optional<UserRating> getRating(String userId, String algorithmId) {
        return Optional.ofNullable(userRatingRepository.findByUserIdAndAlgorithmId(userId, algorithmId));
    }
    @Transactional
    public void calculateAndUpdateAlgorithmGrades(String algorithmId) {
        List<UserRating> userRatings = userRatingRepository.findByAlgorithmId(algorithmId);
        float totalGrade = 0;
        int count = 0;
        // 遍历用户评分列表，计算总分和计数
        for (UserRating userRating : userRatings) {
            totalGrade += userRating.getRating();
            count++;
        }
        // 计算平均分并更新或创建 AlgorithmGrade
        if (count > 0) {
            float averageGrade = totalGrade / count;
            // 查找现有的 AlgorithmGrade
            AlgorithmGrade algorithmGrade = algorithmGradeRepository.findByAlgorithmId(algorithmId);
            if (algorithmGrade == null) {
                algorithmGrade = new AlgorithmGrade();
                algorithmGrade.setAlgorithmId(algorithmId);
            }
            // 更新分数
            algorithmGrade.setGrade(averageGrade);
            algorithmGradeRepository.save(algorithmGrade);
        } else {
            System.out.println("没有找到评分记录，无法计算平均分。");
        }
    }
}
