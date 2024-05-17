import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { CommentResponse } from '../../interfaces/CommentResponse';
import { CommentRequest } from '../../interfaces/CommentRequest';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http:HttpClient) { }

  getCommentById(commentId:number):Observable<CommentResponse>{
    return this.http.get<CommentResponse>(`${environment.urlClassroomApi}/comment/${commentId}`)
    .pipe(catchError(this.handleError));
  }

  getCoursesComments(courseId:number):Observable<CommentResponse[]>{
    return this.http.get<CommentResponse[]>(`${environment.urlClassroomApi}/comment/course/${courseId}`)
    .pipe(catchError(this.handleError));
  }

  getContentsComments(contentId:number):Observable<CommentResponse[]>{
    return this.http.get<CommentResponse[]>(`${environment.urlClassroomApi}/comment/content/${contentId}`)
    .pipe(catchError(this.handleError));
  }

  createCourseComment(commentRequest:CommentRequest, courseId:number):Observable<CommentResponse>{
    return this.http.post<CommentResponse>(`${environment.urlClassroomApi}/comment/course/${courseId}`, commentRequest)
    .pipe(catchError(this.handleError));
  }

  createContentComment(commentRequest:CommentRequest, contentId:number):Observable<CommentResponse>{
    return this.http.post<CommentResponse>(`${environment.urlClassroomApi}/comment/content/${contentId}`, commentRequest)
    .pipe(catchError(this.handleError));
  }

  updateComment(commentRequest:CommentRequest, commentId:number):Observable<String>{
    return this.http.put<String>(`${environment.urlClassroomApi}/comment/${commentId}`, commentRequest)
    .pipe(catchError(this.handleError));
  }

  deleteComment(commentId:number):Observable<String>{
    return this.http.delete<String>(`${environment.urlClassroomApi}/comment/${commentId}`)
    .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => error);
  }
}
