package com.blog.wang.comment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user",url = "http://172.28.23.7:8091")
public interface UserClient {

    @GetMapping("/user/userIds")
    public ResponseEntity<List<String>> getAllUserIds();

}