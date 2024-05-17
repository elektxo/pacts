package com.aula.controller;

import com.aula.dto.CommentRequest;
import com.aula.dto.CommentResponse;
import com.aula.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
@AllArgsConstructor
@CrossOrigin(origins= {"http://localhost:4200"})
public class CommentController {
    private CommentService commentService;

    @PostMapping("comment/course/{courseId}")
    public ResponseEntity<CommentResponse> createCourseComment(@Valid @RequestBody CommentRequest commentRequest, @PathVariable Long courseId) {
        return commentService.createCourseComment(commentRequest, courseId);
    }

    @PostMapping("comment/content/{contentId}")
    public ResponseEntity<CommentResponse> createContentComment(@Valid @RequestBody CommentRequest commentRequest, @PathVariable Long contentId) {
        return commentService.createContentComment(commentRequest, contentId);
    }

    @GetMapping("comment/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @DeleteMapping("comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteCommentById(commentId);
    }

    @PutMapping("comment/{commentId}")
    public ResponseEntity<String> updateComment(@Valid @RequestBody CommentRequest commentRequest,@PathVariable Long commentId) {
        return commentService.updateCommentById(commentRequest,commentId);
    }

    @GetMapping("comment/course/{courseId}")
    public ResponseEntity<List<CommentResponse>> getCourseComments(@PathVariable Long courseId) {
        return commentService.getCommentsByCourseId(courseId);
    }

    @GetMapping("comment/content/{contentId}")
    public ResponseEntity<List<CommentResponse>> getContentComments(@PathVariable Long contentId) {
        return commentService.getCommentsByContentId(contentId);
    }
}