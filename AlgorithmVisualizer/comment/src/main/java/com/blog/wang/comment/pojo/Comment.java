package com.blog.wang.comment.pojo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name="commentId")
    private String commentId;

    @Column(name="userId",nullable = false)
    private String userId;

    @Column(name="algorithmId",nullable = false)
    private String algorithmId;

    @Column(nullable = false)
    private String content;

    @Column(name="commentTime",nullable = false)
    private LocalDateTime commentTime;

    // 存储子评论
    //@Transient // 不在数据库中持久化
    //private List<Comment> replies = new ArrayList<>();

    // 表示父评论的 ID
    @Column(name = "parentId", nullable = true)
    private String parentId; // 用于存储父评论的 ID,为空表示根节点

    // Getters 和 Setters
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public LocalDateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }

    //public List<Comment> getReplies() {
        //return replies;
    //}

    //public void setReplies(List<Comment> replies) {
        //this.replies = replies;
    //}

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
