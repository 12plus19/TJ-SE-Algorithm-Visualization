package com.blog.wang.user.service;

import com.blog.wang.user.pojo.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 存储邮箱和验证码的映射
    private Map<String, VerificationCode> emailVerificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {
        // 生成随机验证码
        String code = generateVerificationCode();
        emailVerificationCodes.put(email, new VerificationCode(code));

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1316477101@qq.com"); // 确保这里的地址与用户名相同
        message.setTo(email);
        message.setSubject("邮箱注册验证码");
        message.setText("您好，这里是算法可视化平台！您的验证码是: " + code);
        mailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 生成6位验证码
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode storedCode = emailVerificationCodes.get(email);
        if (storedCode != null) {
            // 检查验证码是否匹配
            boolean isCodeValid = storedCode.getCode().equals(code);
            // 检查验证码是否在5分钟内有效
            boolean isCodeExpired = LocalDateTime.now().isAfter(storedCode.getTimestamp().plusMinutes(5));
            return isCodeValid && !isCodeExpired;
        }
        return false;
    }

    // 修改后的方法：根据邮箱查找验证码
    public String getVerificationCode(String email) {
        VerificationCode storedCode = emailVerificationCodes.get(email);
        return storedCode != null ? storedCode.getCode() : null; // 返回验证码或null
    }
}