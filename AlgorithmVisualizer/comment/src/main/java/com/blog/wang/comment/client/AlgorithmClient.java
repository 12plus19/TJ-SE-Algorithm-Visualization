package com.blog.wang.comment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "algorithm",url = "http://172.28.23.7:8092")
public interface AlgorithmClient {

    @GetMapping("/algorithm/ids")
    public ResponseEntity<List<String>> getAllAlgorithmIds();

}
