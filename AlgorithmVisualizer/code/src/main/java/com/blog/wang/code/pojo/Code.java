package com.blog.wang.code.pojo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "codes")
public class Code {

    @Id
    @Column(name="codeId")
    private String codeId;

    @Column(name="userId", nullable = false)
    private String userId;

    @Column(name="algorithmId", nullable = false)
    private String algorithmId;

    @Lob // 使用 @Lob 注解,表示长文本
    @Column(nullable = false)
    private String content;

    @Column(name="saveTime", nullable = false)
    private LocalDateTime saveTime;


    //
    public Code() {
        this.codeId = UUID.randomUUID().toString(); // 生成唯一的字符串 ID
    }
    public String getCodeId() {
        return codeId;
    }
    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getAlgorithmId() {
        return algorithmId;
    }
    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getSaveTime() {
        return saveTime;
    }
    public void setSaveTime(LocalDateTime saveTime) {
        this.saveTime = saveTime;
    }
}
