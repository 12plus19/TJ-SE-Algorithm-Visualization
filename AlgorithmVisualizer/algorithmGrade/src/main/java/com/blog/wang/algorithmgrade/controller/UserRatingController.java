package com.blog.wang.algorithmgrade.controller;

import com.blog.wang.algorithmgrade.client.AlgorithmClient;
import com.blog.wang.algorithmgrade.client.UserClient;
import com.blog.wang.algorithmgrade.pojo.UserRating;
import com.blog.wang.algorithmgrade.service.AlgorithmGradeRepository;
import com.blog.wang.algorithmgrade.service.UserRatingRepository;
import com.blog.wang.algorithmgrade.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/algorithmGrade/userRating")
public class UserRatingController {

    @Autowired
    private AlgorithmGradeRepository algorithmGradeRepository;

    @Autowired
    private UserRatingRepository userRatingRepository;

    @Autowired
    private UserRatingService userRatingService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AlgorithmClient algorithmClient;

    @PostMapping
    public ResponseEntity<?> createRating(@RequestParam String userId,
                                                   @RequestParam String algorithmId,
                                                   @RequestParam int rating) {
        // 检查用户是否存在
        ResponseEntity<List<String>> userResponse = userClient.getAllUserIds();
        if (userResponse.getStatusCode().is2xxSuccessful()) {
            List<String> userIds = userResponse.getBody();
            if (userIds == null || !userIds.contains(userId)) {
                return ResponseEntity.badRequest().body("用户不存在");
            }
        } else {
            return ResponseEntity.status(userResponse.getStatusCode()).body("无法检查用户存在性");
        }

        // 检查算法是否存在
        ResponseEntity<List<String>> algorithmResponse = algorithmClient.getAllAlgorithmIds();
        if (algorithmResponse.getStatusCode().is2xxSuccessful()) {
            List<String> algorithmIds = algorithmResponse.getBody();
            if (algorithmIds == null || !algorithmIds.contains(algorithmId)) {
                return ResponseEntity.badRequest().body("算法不存在");
            }
        } else {
            return ResponseEntity.status(algorithmResponse.getStatusCode()).body("无法检查算法存在性");
        }
        if(rating<1||rating>5){
            return new ResponseEntity<>("评分不合法",HttpStatus.BAD_REQUEST);
        }
        UserRating createdRating = userRatingService.createRating(userId, algorithmId, rating);
        userRatingService.calculateAndUpdateAlgorithmGrades(algorithmId);
        return ResponseEntity.ok(createdRating);
    }
    @PutMapping
    public ResponseEntity<?> updateRating(@RequestParam String userId,
                                                   @RequestParam String algorithmId,
                                                   @RequestParam int rating) {
        if(rating<1||rating>5){
            return new ResponseEntity<>("评分不合法",HttpStatus.BAD_REQUEST);
        }
        UserRating updatedRating = userRatingService.updateRating(userId, algorithmId, rating);
        userRatingService.calculateAndUpdateAlgorithmGrades(algorithmId);
        if (updatedRating != null) {
            return ResponseEntity.ok(updatedRating);
        } else {
            return new ResponseEntity<>("评分未找到", HttpStatus.NOT_FOUND); // 评分未找到
        }
    }
    // 查看评分
    @GetMapping
    public ResponseEntity<?> getRating(@RequestParam String userId,
                                                @RequestParam String algorithmId) {
        Optional<UserRating> rating = userRatingService.getRating(userId, algorithmId);
        if(rating.isEmpty()){
                return new ResponseEntity<>("评分未找到", HttpStatus.NOT_FOUND); // 返回 String 消息
        }else{
            return ResponseEntity.ok(rating.get());
        }
    }
}
