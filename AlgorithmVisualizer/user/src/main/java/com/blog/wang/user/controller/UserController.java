package com.blog.wang.user.controller;

import com.blog.wang.user.client.LearningProgressClient;
import com.blog.wang.user.pojo.User;
import com.blog.wang.user.service.EmailService;
import com.blog.wang.user.service.UserRepository;
import com.blog.wang.user.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LearningProgressClient learningProgressClient;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestParam String code, @RequestBody User user) {
        // 检查邮箱是否已被使用
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被其他账号使用");
        }
        // 验证验证码
        ResponseEntity<String> verificationResponse = verifyCode(user.getEmail(), code);
        if (!verificationResponse.getStatusCode().is2xxSuccessful()) {
            return verificationResponse; // 返回验证码验证失败的响应
        }
        user.setUserId("生成userId");
        userRepository.save(user);
        // 调用学习进度微服务创建学习进度元素
        ResponseEntity<String> learningProgressResponse = learningProgressClient.createLearningProgress(user.getUserId());
        if (!learningProgressResponse.getStatusCode().is2xxSuccessful()) {
            //throw new RuntimeException("用户注册成功，但学习进度元素创建失败");
            return ResponseEntity.status(500).body("用户注册成功，但学习进度元素创建失败");
        }
        return ResponseEntity.ok("用户注册成功");
    }

    @PostMapping("/sendCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("邮箱已被其他账号使用");
        }
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok("验证码已发送到邮箱");
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = emailService.verifyCode(email, code);
        // 检查验证码是否存在
        if (emailService.getVerificationCode(email) == null) {
            return ResponseEntity.badRequest().body("还未发送验证码");
        }
        if (isValid) {
            return ResponseEntity.ok("验证码验证成功");
        } else {
            return ResponseEntity.badRequest().body("验证码无效或已过期");
        }
    }
    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // 生成JWT
                Map<String, Object> claims = Map.of("userId", user.getUserId()); // 你可以根据需要添加更多的声明
                String jwt = JwtUtils.generateJwt(claims);
                // 返回JWT和管理员信息
                // 设置JWT到响应头（可选）
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", jwt);

                // 返回用户实体和JWT
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(user);
            } else {
                return ResponseEntity.badRequest().body("密码错误");
            }
        } else {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
    }
    // 查看个人信息接口
    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam String userId,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        // 查找用户
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 验证旧密码
            if (user.getPassword().equals(oldPassword)) {
                // 更新密码
                user.setPassword(newPassword);
                userRepository.save(user);
                return ResponseEntity.ok("密码修改成功");
            } else {
                return ResponseEntity.badRequest().body("旧密码错误");
            }
        } else {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/userIds")
    public ResponseEntity<List<String>> getAllUserIds() {
        List<String> userIds = userRepository.findAll().stream()
                .map(User::getUserId) // 假设 User 类中有 getUserId() 方法
                .collect(Collectors.toList());
        return ResponseEntity.ok(userIds);
    }
}
