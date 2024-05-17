package com.aula.service;

import com.aula.dto.CommentRequest;
import com.aula.dto.CommentResponse;
import com.aula.dto.converter.CommentConverter;
import com.aula.entity.Comment;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.repository.CommentRepository;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;
    private final ContentRepository contentRepository;

    public CommentService(CommentRepository commentRepository, CourseRepository courseRepository, ContentRepository contentRepository) {
        this.commentRepository = commentRepository;
        this.courseRepository = courseRepository;
        this.contentRepository = contentRepository;
    }

    public ResponseEntity<CommentResponse> createCourseComment(CommentRequest commentRequest, Long courseId) {
        try{
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            Comment comment = new Comment();
            comment.setText(commentRequest.getText());
            comment.setCreatorUsername(commentRequest.getCreatorUsername());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(null);
            comment.setCourse(course);
            comment.setContent(null);
            comment = commentRepository.save(comment);
            return ResponseEntity.ok(new CommentResponse(comment.getId(), comment.getText(), comment.getCreatorUsername(), comment.getCreatedAt(), comment.getUpdatedAt()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<CommentResponse> createContentComment(CommentRequest commentRequest, Long contentId) {
        try{
            Content content = contentRepository.findById(contentId).orElse(null);
            if (content == null) {
                return ResponseEntity.notFound().build();
            }
            Comment comment = new Comment();
            comment.setText(commentRequest.getText());
            comment.setCreatorUsername(commentRequest.getCreatorUsername());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(null);
            comment.setContent(content);
            comment.setCourse(null);
            comment = commentRepository.save(comment);
            return ResponseEntity.ok(new CommentResponse(comment.getId(), comment.getText(), comment.getCreatorUsername(), comment.getCreatedAt(), comment.getUpdatedAt()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> deleteCommentById(Long commentId) {
        try {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok("\"Comentario borrado con éxito con ID: " + commentId + "\"");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("\"Error al eliminar el comentario: " + e.getMessage() + "\"");
        }
    }

    public ResponseEntity<String> updateCommentById(CommentRequest commentRequest, Long commentId) {
        try {
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }
            comment.setText(commentRequest.getText());
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
            return ResponseEntity.ok("\"Comentario actualizado con éxito con ID: " + comment.getId() + "\"");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("\"Error al actualizar el comentario: " + e.getMessage() + "\"");
        }
    }

    public ResponseEntity<CommentResponse> getCommentById(Long commentId) {
        try {
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new CommentResponse(comment.getId(), comment.getText(), comment.getCreatorUsername(), comment.getCreatedAt(), comment.getUpdatedAt()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<CommentResponse>> getCommentsByCourseId(Long courseId) {
        try {
            List<Comment> comments = commentRepository.findByCourseId(courseId);
            if (comments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(CommentConverter.ListEntityToListDTO(comments));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<CommentResponse>> getCommentsByContentId(Long contentId) {
        try {
            List<Comment> comments = commentRepository.findByContentId(contentId);
            if (comments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(CommentConverter.ListEntityToListDTO(comments));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}