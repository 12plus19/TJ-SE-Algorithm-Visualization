package com.blog.wang.algorithm.service;

import com.blog.wang.algorithm.pojo.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlgorithmRepository extends JpaRepository<Algorithm, String> {
}
