package com.blog.wang.code.service;


import com.blog.wang.code.pojo.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CodeService {

    @Autowired
    private CodeRepository codeRepository;

    // 创建代码历史记录
    public Code createCode(String userId, String algorithmId, String content) {
        Code code = new Code();
        code.setUserId(userId);
        code.setAlgorithmId(algorithmId);
        code.setContent(content);
        code.setSaveTime(LocalDateTime.now());
        return codeRepository.save(code);
    }

    // 获取所有代码历史记录
    public List<Code> getAllCodes() {
        return codeRepository.findAll();
    }

    // 根据 ID 获取代码历史记录
    public Code getCodeById(String codeId) {
        return codeRepository.findById(codeId).orElse(null);
    }

    // 更新代码历史记录
    public Code updateCode(String codeId, String content) {
        Code code = getCodeById(codeId);
        if (code != null) {
            code.setContent(content);
            code.setSaveTime(LocalDateTime.now()); // 更新保存时间
            return codeRepository.save(code);
        }
        return null; // 如果代码不存在，返回 null
    }

    // 删除代码历史记录
    public boolean deleteCode(String codeId) {
        Code code = getCodeById(codeId);
        if (code != null) {
            codeRepository.deleteById(codeId);
            return true;
        }
        return false;
    }
    // 根据 userId 和 algorithmId 获取代码历史记录
    public List<Code> getCodesByUserIdAndAlgorithmId(String userId, String algorithmId) {
        return codeRepository.findByUserIdAndAlgorithmId(userId, algorithmId);
    }
}
