import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { CourseRequest } from '../../interfaces/CourseRequest';
import { CourseResponse } from '../../interfaces/CourseResponse';


@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  FormCreateCourse: CourseResponse = {
    title: '',
    description: '',
    price: 0,
    free: true,
    teacherId: '',
    contents: []
  }

  //Get all the courses by student
  getCoursesByStudent(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(environment.urlClassroomApi + "/courses/student/" + this.auth.getProfile()?.id).pipe(
      catchError(this.handleError)
    );
  }

  getCourseByStudent(courseId:number): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(environment.urlClassroomApi + "/courses/"+ courseId + "/student/" + this.auth.getProfile()?.id).pipe(
      catchError(this.handleError)
    );
  }

  //Get all the courses from a teacher
  getAllTeacherCourses(teacherId: String): Observable<CourseResponse[]>{
    return this.http.get<CourseResponse[]>(`${environment.urlClassroomApi}/courses/teacher/${teacherId}/courses`).pipe(
      catchError(this.handleError)
    );
  }

  //Get all the courses
  getAllCourses(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(environment.urlClassroomApi + '/courses').pipe(
      catchError(this.handleError)
    );
  }

  // Add a student to a course
  addStudentToCourse(courseId: number | undefined): Observable<any> {
    return this.http.put<CourseResponse[]>(`${environment.urlClassroomApi}/courses/${courseId}/add-student?studentId=${this.auth.getProfile()?.id}`, {})
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete a student from a course
  removeStudentFromCourse(courseId: number): Observable<any> {
    return this.http.put<CourseResponse[]>(`${environment.urlClassroomApi}/courses/${courseId}/remove-student?studentId=${this.auth.getProfile()?.id}`, {})
      .pipe(
        catchError(this.handleError)
      );
  }


  //Get course via Id
  getCourseById(courseId: number): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(`${environment.urlClassroomApi}/courses/${courseId}`).pipe(
      catchError(this.handleError)
    );
  }

  //Create the course
  createCourse(data: FormData): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${environment.urlClassroomApi}/courses`, data).pipe(
      tap((response) => {
        console.log(response);
      }),
      catchError(this.handleError)
    );
  }

  //Delete courses via id
  deleteCourse(courseId: number): Observable<string> {
    return this.http.delete<string>(`${environment.urlClassroomApi}/courses/${courseId}`).pipe(
      catchError(this.handleError)
    );
  }

  //Update an specific course
  updateCourse(courseId: number, data:FormData): Observable<string> {
    return this.http.put<string>(`${environment.urlClassroomApi}/courses/${courseId}`, data)
  }

  //Remove content from a course via Id
  removeContentFromCourse(courseId: number, contentId: number): Observable<any> {
    return this.http.put<any>(`${environment.urlClassroomApi}/courses/${courseId}/remove-content?contentId=${contentId}`, {}).pipe(
      catchError(this.handleError)
    )
  }

  // Adds content to a course
  addContentToCourse(courseId: number, contentId: number): Observable<any> {
    return this.http.put<any>(`${environment.urlClassroomApi}/courses/${courseId}/add-content?contentId=${contentId}`, {}).pipe(
      catchError(this.handleError)
    )
  }


  //Get the course by keyword
  public getDataByKey(key: string): Observable<any> {
    return this.http.get<any>(environment.urlClassroomApi + "/courses/search?filter=" + key)
      .pipe(
        catchError(this.handleError)
      );
  }

  public updateCompleteContent(courseId: number, contentId: number, completed:boolean): Observable<String> {
    let contentCompleted = {contentId: contentId, courseId: courseId, studentId: this.auth.getProfile()?.id, completed: completed};
    return this.http.put<String>(`${environment.urlClassroomApi}/course-registration-contents/complete`, contentCompleted)
      .pipe(
        catchError(this.handleError)
      );
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
