package com.blog.wang.learningprogress.service;

import com.blog.wang.learningprogress.pojo.LearningProgress;
import com.blog.wang.learningprogress.pojo.LearningProgressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, LearningProgressId> {
    List<LearningProgress> findByUserId(String userId);
}
