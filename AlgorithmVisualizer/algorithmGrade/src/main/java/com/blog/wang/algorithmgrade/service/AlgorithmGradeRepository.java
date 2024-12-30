package com.blog.wang.algorithmgrade.service;

import com.blog.wang.algorithmgrade.pojo.AlgorithmGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlgorithmGradeRepository extends JpaRepository<AlgorithmGrade, String> {
    AlgorithmGrade findByAlgorithmId(String algorithmId);
}
