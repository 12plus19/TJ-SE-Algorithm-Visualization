package com.blog.wang.user.pojo;

import java.time.LocalDateTime;

public class VerificationCode {

    private String code;
    private LocalDateTime timestamp;

    public VerificationCode(String code) {
        this.code = code;
        this.timestamp = LocalDateTime.now(); // 记录当前时间
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}