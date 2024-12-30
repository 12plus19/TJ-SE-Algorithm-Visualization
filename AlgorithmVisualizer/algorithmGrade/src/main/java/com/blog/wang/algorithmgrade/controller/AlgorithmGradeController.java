package com.blog.wang.algorithmgrade.controller;

import com.blog.wang.algorithmgrade.pojo.AlgorithmGrade;
import com.blog.wang.algorithmgrade.pojo.UserRating;
import com.blog.wang.algorithmgrade.service.AlgorithmGradeRepository;
import com.blog.wang.algorithmgrade.service.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/algorithmGrade")
public class AlgorithmGradeController {

    @Autowired
    private AlgorithmGradeRepository algorithmGradeRepository;

    @Autowired
    private UserRatingRepository userRatingRepository;

    @GetMapping("averageGrade")
    public ResponseEntity<?> getRating(@RequestParam String algorithmId) {
        Optional<AlgorithmGrade> grade = Optional.ofNullable(algorithmGradeRepository.findByAlgorithmId(algorithmId));
        if(grade.isEmpty()){
            return new ResponseEntity<>("该算法暂无评分", HttpStatus.NOT_FOUND); // 返回 String 消息
        }else{
            return ResponseEntity.ok(grade.get());
        }
    }

}
