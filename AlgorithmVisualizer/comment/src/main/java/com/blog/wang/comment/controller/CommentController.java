package com.blog.wang.comment.controller;

import com.blog.wang.comment.client.AlgorithmClient;
import com.blog.wang.comment.client.UserClient;
import com.blog.wang.comment.dto.CommentDTO;
import com.blog.wang.comment.pojo.Comment;
import com.blog.wang.comment.service.CommentRepository;
import com.blog.wang.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AlgorithmClient algorithmClient;
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO) {
        // 检查用户是否存在
        ResponseEntity<List<String>> userResponse = userClient.getAllUserIds();
        if (userResponse.getStatusCode().is2xxSuccessful()) {
            List<String> userIds = userResponse.getBody();
            if (userIds == null || !userIds.contains(commentDTO.getUserId())) {
                return ResponseEntity.badRequest().body("用户不存在");
            }
        } else {
            return ResponseEntity.status(userResponse.getStatusCode()).body("无法检查用户存在性");
        }
        // 检查算法是否存在
        ResponseEntity<List<String>> algorithmResponse = algorithmClient.getAllAlgorithmIds();
        if (algorithmResponse.getStatusCode().is2xxSuccessful()) {
            List<String> algorithmIds = algorithmResponse.getBody();
            if (algorithmIds == null || !algorithmIds.contains(commentDTO.getAlgorithmId())) {
                return ResponseEntity.badRequest().body("算法不存在");
            }
        } else {
            return ResponseEntity.status(algorithmResponse.getStatusCode()).body("无法检查算法存在性");
        }
        //检查父节点是否存在
        if(!commentDTO.getParentId().isEmpty()){
            if (!commentService.parentCommentExists(commentDTO.getParentId())) {
                return ResponseEntity.badRequest().body("父评论不存在");
            }
        }
        Comment createdComment = commentService.createComment(commentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    // 根据 algorithmId 查找所有评论
    @GetMapping("/algorithm/{algorithmId}")
    public ResponseEntity<?> getCommentsByAlgorithmId(@PathVariable String algorithmId) {
        ResponseEntity<List<String>> algorithmResponse = algorithmClient.getAllAlgorithmIds();
        if (algorithmResponse.getStatusCode().is2xxSuccessful()) {
            List<String> algorithmIds = algorithmResponse.getBody();
            if (algorithmIds == null || !algorithmIds.contains(algorithmId)){
                return ResponseEntity.badRequest().body("算法不存在");
            }
        } else {
            return ResponseEntity.status(algorithmResponse.getStatusCode()).body("无法检查算法存在性");
        }
        List<Comment> comments = commentService.getCommentsByAlgorithmId(algorithmId);
        return ResponseEntity.ok(comments);
    }

    // 根据 algorithmId 和 parentId 查找其子评论
    @GetMapping("/algorithm/{algorithmId}/parent/{parentId}")
    public ResponseEntity<?> getCommentsByAlgorithmIdAndParentId(@PathVariable String algorithmId, @PathVariable String parentId) {
        ResponseEntity<List<String>> algorithmResponse = algorithmClient.getAllAlgorithmIds();
        if (algorithmResponse.getStatusCode().is2xxSuccessful()) {
            List<String> algorithmIds = algorithmResponse.getBody();
            if (algorithmIds == null || !algorithmIds.contains(algorithmId)){
                return ResponseEntity.badRequest().body("算法不存在");
            }
        } else {
            return ResponseEntity.status(algorithmResponse.getStatusCode()).body("无法检查算法存在性");
        }
        if (!commentService.parentCommentExists(parentId)) {
            return ResponseEntity.badRequest().body("父评论不存在");
        }else if(!commentService.getCommentById(parentId).get().getAlgorithmId().equals(algorithmId)){
            return ResponseEntity.badRequest().body("父评论不存在");
        }
        List<Comment> comments = commentService.getCommentsByAlgorithmIdAndParentId(algorithmId, parentId);
        return ResponseEntity.ok(comments);
    }
    // 根据 commentId 获取评论
    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable String commentId) {
        Optional<Comment> comment = commentService.getCommentById(commentId);
        if(comment.isPresent()){
            return ResponseEntity.ok(comment.get());
        }else{
            return new ResponseEntity<>("评论未找到",HttpStatus.NOT_FOUND);
        }
    }
    // 根据 commentId 删除评论及其子评论
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        Optional<Comment> comment = commentService.getCommentById(commentId);
        if(comment.isEmpty()) {
            return new ResponseEntity<>("评论未找到",HttpStatus.NOT_FOUND);
        }
        commentService.deleteCommentAndChildren(commentId);
        return ResponseEntity.noContent().build(); // 返回204 No Content
    }


}
