package com.blog.wang.code.controller;

import com.blog.wang.code.client.AlgorithmClient;
import com.blog.wang.code.client.UserClient;
import com.blog.wang.code.pojo.Code;
import com.blog.wang.code.service.CodeRepository;
import com.blog.wang.code.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/code")
public class CodeController {

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private CodeService codeService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AlgorithmClient algorithmClient;
    // 创建代码历史记录
    @PostMapping
    public ResponseEntity<?> createCode(@RequestParam String userId,
                                           @RequestParam String algorithmId,
                                           @RequestBody String content) {
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
        Code createdCode = codeService.createCode(userId, algorithmId, content);
        return ResponseEntity.ok(createdCode);
    }

    // 根据 userId 和 algorithmId 获取代码历史记录
    @GetMapping("codes")
    public ResponseEntity<List<Code>> getCodesByUserIdAndAlgorithmId(@RequestParam String userId,
                                                                     @RequestParam String algorithmId) {
        List<Code> codes = codeService.getCodesByUserIdAndAlgorithmId(userId, algorithmId);
        return ResponseEntity.ok(codes);
    }

    // 根据 codeID 获取代码历史记录
    @GetMapping("/{codeId}")
    public ResponseEntity<?> getCodeById(@PathVariable String codeId) {
        Code code = codeService.getCodeById(codeId);
        if (code != null) {
            return ResponseEntity.ok(code);
        } else {
            return new ResponseEntity<>("未找到代码记录", HttpStatus.NOT_FOUND);
        }
    }

    // 更新代码历史记录
    @PutMapping("/{codeId}")
    public ResponseEntity<?> updateCode(@PathVariable String codeId,
                                           @RequestBody String content) {
        Code updatedCode = codeService.updateCode(codeId, content);
        if (updatedCode != null) {
            return ResponseEntity.ok(updatedCode);
        } else {
            return new ResponseEntity<>("未找到代码记录", HttpStatus.NOT_FOUND);
        }
    }

    // 删除代码历史记录
    @DeleteMapping("/{codeId}")
    public ResponseEntity<?> deleteCode(@PathVariable String codeId) {
        boolean delete=codeService.deleteCode(codeId);
        if(delete){
            return new ResponseEntity<>("删除成功", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("未找到代码记录", HttpStatus.NOT_FOUND);
        }
    }
}
