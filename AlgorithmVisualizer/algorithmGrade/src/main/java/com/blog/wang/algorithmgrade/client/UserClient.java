package com.blog.wang.algorithmgrade.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user",url = "http://localhost:8091")
public interface UserClient {

    @GetMapping("/user/userIds")
    public ResponseEntity<List<String>> getAllUserIds();

}