package com.blog.wang.learningprogress.controller;

import com.blog.wang.learningprogress.client.AlgorithmClient;
import com.blog.wang.learningprogress.pojo.LearningProgress;
import com.blog.wang.learningprogress.pojo.LearningProgressId;
import com.blog.wang.learningprogress.service.LearningProgressRepository;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/learningProgress")
public class LearningProgressController {

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private AlgorithmClient algorithmClient;

    @PostMapping("/allAlgorithm/{userId}")
    public ResponseEntity<String> createLearningProgress(@PathVariable String userId) {
        // 获取所有算法 ID
        ResponseEntity<List<String>> response = algorithmClient.getAllAlgorithmIds();
        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("无法获取算法 ID");
        }
        List<String> algorithmIds = response.getBody();
        if (algorithmIds == null || algorithmIds.isEmpty()) {
            return ResponseEntity.badRequest().body("没有可用的算法 ID");
        }
        // 为每个算法创建学习进度
        for (String algorithmId : algorithmIds) {
            LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
            // 检查是否已存在学习进度
            if (learningProgressRepository.existsById(learningProgressId)) {
                return ResponseEntity.badRequest().body("学习进度已存在");
            }
            LearningProgress learningProgress = getLearningProgress(userId, algorithmId, learningProgressId);
            // 保存到数据库
            learningProgressRepository.save(learningProgress);
        }

        return ResponseEntity.ok("学习进度元素创建成功");
    }

    private static LearningProgress getLearningProgress(String userId, String algorithmId, LearningProgressId learningProgressId) {
        LearningProgress learningProgress = new LearningProgress();
        learningProgress.setId(learningProgressId); // 设置复合主键
        learningProgress.setUserId(userId); // 设置 userId
        learningProgress.setAlgorithmId(algorithmId); // 设置 algorithmId
        learningProgress.setProgress(0); // 初始化进度为 0
        learningProgress.setRead(false); // 初始化为未读
        learningProgress.setWatched(false); // 初始化为未观看
        learningProgress.setWritten(false); // 初始化为未编写
        learningProgress.setDone(false); // 初始化为未完成
        return learningProgress;
    }

    @PutMapping("/hasRead")
    public ResponseEntity<String> hasFinishedRead(@Param String userId,@Param String algorithmId) {
        LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
        Optional<LearningProgress> learningProgressOptional = learningProgressRepository.findById(learningProgressId);
        if (learningProgressOptional.isPresent()) {
            LearningProgress learningProgress = learningProgressOptional.get();
            if(learningProgress.isRead()) {
                return ResponseEntity.ok("您已完成阅读");
            }else {
                learningProgress.setRead(true);
                learningProgress.setProgress(learningProgress.getProgress() + 0.25f);
                learningProgressRepository.save(learningProgress);
                return ResponseEntity.ok("已完成阅读，进度增加25%！");
            }
        } else {
            return new ResponseEntity<>("学习进度不存在", HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/hasWatched")
    public ResponseEntity<String> hasFinishedWatched(@Param String userId,@Param String algorithmId){
        LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
        Optional<LearningProgress> learningProgressOptional = learningProgressRepository.findById(learningProgressId);
        if (learningProgressOptional.isPresent()) {
            LearningProgress learningProgress = learningProgressOptional.get();
            if(learningProgress.isWatched()) {
                return ResponseEntity.ok("您已完成算法可视化动画观看");
            }else {
                learningProgress.setWatched(true);
                learningProgress.setProgress(learningProgress.getProgress() + 0.25f);
                learningProgressRepository.save(learningProgress);
                return ResponseEntity.ok("已完成算法可视化动画观看，进度增加25%！");
            }
        } else {
            return new ResponseEntity<>("学习进度不存在", HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/hasWritten")
    public ResponseEntity<String> hasFinishedWritten(@Param String userId,@Param String algorithmId){
        LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
        Optional<LearningProgress> learningProgressOptional = learningProgressRepository.findById(learningProgressId);
        if (learningProgressOptional.isPresent()) {
            LearningProgress learningProgress = learningProgressOptional.get();
            if(learningProgress.isWritten()) {
                return ResponseEntity.ok("您已完成算法代码编写");
            }else {
                learningProgress.setWritten(true);
                learningProgress.setProgress(learningProgress.getProgress() + 0.25f);
                learningProgressRepository.save(learningProgress);
                return ResponseEntity.ok("已完成算法代码编写，进度增加25%！");
            }
        } else {
            return new ResponseEntity<>("学习进度不存在", HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/hasDone")
    public ResponseEntity<String> hasFinishedDone(@Param String userId,@Param String algorithmId){
        LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
        Optional<LearningProgress> learningProgressOptional = learningProgressRepository.findById(learningProgressId);
        if (learningProgressOptional.isPresent()) {
            LearningProgress learningProgress = learningProgressOptional.get();
            if(learningProgress.isDone()) {
                return ResponseEntity.ok("您已完成算法练习");
            }else {
                learningProgress.setDone(true);
                learningProgress.setProgress(learningProgress.getProgress() + 0.25f);
                learningProgressRepository.save(learningProgress);
                return ResponseEntity.ok("已完成算法练习，进度增加25%！");
            }
        } else {
            return new ResponseEntity<>("学习进度不存在", HttpStatus.NOT_FOUND);
        }
    }
    // 根据用户ID和算法ID获取学习进度
    @GetMapping()
    public ResponseEntity<?> getLearningProgress(@Param String userId, @Param String algorithmId) {

        LearningProgressId learningProgressId = new LearningProgressId(userId, algorithmId);
        Optional<LearningProgress> learningProgressOptional = learningProgressRepository.findById(learningProgressId);

        if (learningProgressOptional.isPresent()) {
            return ResponseEntity.ok(learningProgressOptional.get());
        } else {
            return new ResponseEntity<>("学习进度不存在",HttpStatus.NOT_FOUND);
        }
    }
    // 根据用户ID查找所有学习进度
    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllLearningProgressByUserId(@PathVariable String userId) {
        List<LearningProgress> learningProgressList = learningProgressRepository.findByUserId(userId);

        if (learningProgressList.isEmpty()) {
            return new ResponseEntity<>("学习进度不存在",HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(learningProgressList);
        }
    }
}
