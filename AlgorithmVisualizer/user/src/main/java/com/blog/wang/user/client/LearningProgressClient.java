package com.blog.wang.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "learningProgress",url = "http://172.28.23.7:8094") // 这里的名称应与学习进度微服务的服务名称一致
public interface LearningProgressClient {

    @PostMapping("/learningProgress/allAlgorithm/{userId}")
    ResponseEntity<String> createLearningProgress(@PathVariable("userId") String userId);
}
