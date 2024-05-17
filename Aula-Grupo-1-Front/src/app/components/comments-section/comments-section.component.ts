import { Component, Input, OnInit } from '@angular/core';
import { CommentResponse } from '../../interfaces/CommentResponse';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommentService } from '../../services/comments/comment.service';
import { CommentRequest } from '../../interfaces/CommentRequest';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-comments-section',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, DatePipe],
  templateUrl: './comments-section.component.html',
  styleUrls: ['./comments-section.component.css']
})
export class CommentsSectionComponent implements OnInit {
  @Input() courseId?: number;
  @Input() contentId?: number;

  comments: CommentResponse[] = [];
  commentForm!: FormGroup;
  username!: string;
  errorMessage!: string;
  editingCommentId: number | null = null;

  constructor(private commentService: CommentService, private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit() {
    this.loadComments();
    this.username = this.authService.getProfile()?.username || '';
    this.commentForm = this.fb.group({
      text: ['', Validators.required],
      creatorUsername: [this.username, [Validators.required]]
    });
  }

  startEditing(comment: CommentResponse) {
    this.editingCommentId = comment.id;
  }

  stopEditing(comment: CommentResponse) {
    const commentRequest: CommentRequest = {
      text: comment.text,
      creatorUsername: comment.creatorUsername
    };
    this.commentService.updateComment(commentRequest, comment.id).subscribe({
      next: () => {
        this.loadComments();
        this.editingCommentId = null;
        console.log('Comentario actualizado exitosamente');
      },
      error: (error) => {
        console.error('Error al actualizar el comentario:', error);
      }
    });
  }

  deleteComment(commentId: number) {
    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        this.loadComments();
        console.log('Comentario eliminado exitosamente');
      },
      error: (error) => {
        console.error('Error al eliminar el comentario:', error);
      }
    });
  }

  onSubmit() {
    this.createComment();
  }

  createComment() {
    const commentRequest: CommentRequest = this.commentForm.value;
    if (this.courseId) {
      this.commentService.createCourseComment(commentRequest, this.courseId).subscribe({
        next: (comment) => {
          this.loadComments();
          this.commentForm.reset();
        },
        error: (error) => {
          console.error('Error al crear el comentario:', error);
        }
      });
    } else if (this.contentId) {
      this.commentService.createContentComment(commentRequest, this.contentId).subscribe({
        next: (comment) => {
          this.loadComments();
          this.commentForm.reset();
        },
        error: (error) => {
          console.error('Error al crear el comentario:', error);
        }
      });
    }
  }

  loadComments() {
    if (this.courseId) {
      this.commentService.getCoursesComments(this.courseId).subscribe({
        next: (comments) => {
          this.comments = comments;
        },
        error: (error:HttpErrorResponse) => {
          if (error.status === 404) {
            this.comments = [];
          } else {
            this.errorMessage = 'An error occurred: ' + error.message;
          }
        }
      });
    } else if (this.contentId) {
      this.commentService.getContentsComments(this.contentId).subscribe({
        next: (comments) => this.comments = comments,
        error: (error) => console.error('Error:', error)
      });
    }
  }
}
