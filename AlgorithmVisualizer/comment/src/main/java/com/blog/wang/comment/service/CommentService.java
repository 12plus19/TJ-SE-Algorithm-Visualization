package com.blog.wang.comment.service;

import com.blog.wang.comment.dto.CommentDTO;
import com.blog.wang.comment.pojo.Comment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository; // 假设您有一个 CommentRepository 接口

    public Comment createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID().toString()); // 生成唯一的评论 ID
        comment.setUserId(commentDTO.getUserId());
        comment.setAlgorithmId(commentDTO.getAlgorithmId());
        comment.setContent(commentDTO.getContent());
        comment.setCommentTime(LocalDateTime.now());
        comment.setParentId(commentDTO.getParentId());

        return commentRepository.save(comment); // 保存评论到数据库
    }
    public boolean parentCommentExists(String parentId) {
        return commentRepository.existsByCommentId(parentId); // 检查父评论是否存在
    }
    public List<Comment> getCommentsByAlgorithmId(String algorithmId) {
        return commentRepository.findByAlgorithmId(algorithmId); // 根据 algorithmId 查找评论
    }

    public List<Comment> getCommentsByAlgorithmIdAndParentId(String algorithmId, String parentId) {
        return commentRepository.findByAlgorithmIdAndParentId(algorithmId, parentId); // 根据 algorithmId 和 parentId 查找子评论
    }
    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId); // 根据 commentId 查找评论
    }

    @Transactional
    public void deleteCommentAndChildren(String commentId) {
        // 先查找要删除的评论
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            // 查找所有子评论
            List<Comment> children = commentRepository.findByParentId(commentId);
            // 递归删除子评论
            for (Comment child : children) {
                deleteCommentAndChildren(child.getCommentId());
            }
            // 删除当前评论
            commentRepository.delete(comment.get());
        }
    }
}
