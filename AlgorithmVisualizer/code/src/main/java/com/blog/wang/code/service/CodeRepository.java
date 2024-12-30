package com.blog.wang.code.service;


import com.blog.wang.code.pojo.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, String> {
    List<Code> findByUserIdAndAlgorithmId(String userId, String algorithmId);
}
