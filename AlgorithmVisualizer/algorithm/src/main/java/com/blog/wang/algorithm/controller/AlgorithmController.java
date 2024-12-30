package com.blog.wang.algorithm.controller;

import com.blog.wang.algorithm.pojo.Algorithm;
import com.blog.wang.algorithm.service.AlgorithmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {

    @Autowired
    private AlgorithmRepository algorithmRepository;

    // 获取所有算法ID
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getAllAlgorithmIds() {
        List<Algorithm> algorithms = algorithmRepository.findAll();
        List<String> algorithmIds = algorithms.stream()
                .map(Algorithm::getAlgorithmId) // 假设 Algorithm 类有 getId() 方法
                .collect(Collectors.toList());
        return ResponseEntity.ok(algorithmIds);
    }
    // 获取所有算法信息
    @GetMapping("/all")
    public ResponseEntity<List<Algorithm>> getAllAlgorithms() {
        List<Algorithm> algorithms = algorithmRepository.findAll();
        return ResponseEntity.ok(algorithms);
    }
    // 根据算法ID获取算法信息
    @GetMapping("/info/{algorithmId}")
    public ResponseEntity<?> getAlgorithmById(@PathVariable String algorithmId) {
        Optional<Algorithm> algorithmOptional = algorithmRepository.findById(algorithmId);
        if (algorithmOptional.isPresent()) {
            return ResponseEntity.ok(algorithmOptional.get());
        } else {
            return new ResponseEntity<>("该算法不存在", HttpStatus.NOT_FOUND);
        }
    }
}
