package com.blog.wang.user.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name="userId")
    private String userId;

    @Column(nullable = false)
    @Email(message = "邮箱格式不正确")
    private String email;

    @Column(nullable = false)
    @Size(min = 6, message = "密码至少需要6个字符")
    private String password;

    //
    public User() {
        this.userId = UUID.randomUUID().toString(); // 生成唯一的字符串 ID
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = UUID.randomUUID().toString();
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
