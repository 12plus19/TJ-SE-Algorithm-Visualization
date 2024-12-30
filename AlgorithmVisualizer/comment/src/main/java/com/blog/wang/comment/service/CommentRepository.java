package com.blog.wang.comment.service;

import com.blog.wang.comment.pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    boolean existsByCommentId(String parentId);

    List<Comment> findByAlgorithmId(String algorithmId);

    List<Comment> findByAlgorithmIdAndParentId(String algorithmId, String parentId);

    List<Comment> findByParentId(String commentId);
}
