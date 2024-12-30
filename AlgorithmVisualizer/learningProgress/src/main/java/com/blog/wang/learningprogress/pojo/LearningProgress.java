package com.blog.wang.learningprogress.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "learningProgresses")
@IdClass(LearningProgressId.class) // 指定复合主键类
public class LearningProgress {

    @Id
    @Column(name="userId")
    private String userId;

    @Id
    @Column(name="algorithmId")
    private String algorithmId;

    @Column(nullable = false)
    private float progress;

    @Column(name = "`read`",nullable = false)
    private boolean read;

    @Column(nullable = false)
    private boolean watched;

    @Column(nullable = false)
    private boolean written;

    @Column(nullable = false)
    private boolean done;

    //
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
    public float getProgress() {
        return progress;
    }
    public void setProgress(float progress) {
        this.progress = progress;
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
    public boolean isWatched() {
        return watched;
    }
    public void setWatched(boolean watched) {
        this.watched = watched;
    }
    public boolean isWritten() {
        return written;
    }
    public void setWritten(boolean written) {
        this.written = written;
    }
    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }
    public void setId(LearningProgressId learningProgressId) {
    }
}
