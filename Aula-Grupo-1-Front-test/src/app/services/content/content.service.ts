import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { ResponseContent } from '../../interfaces/ResponseContent';
import { environment } from '../../../environments/environment.development';
import { RequestContent } from '../../interfaces/RequestContent';

@Injectable({
  providedIn: 'root'
})
export class ContentService {

  constructor(private http: HttpClient) { }

  getTeacherContents(teacherId: string): Observable<ResponseContent[]> {
    return this.http.get<ResponseContent[]>(`${environment.urlClassroomApi}/teacher/${teacherId}/contents`)
      .pipe(catchError(this.handleError));
  }

  getContentById(contentId: number): Observable<ResponseContent> {
    return this.http.get<ResponseContent>(`${environment.urlClassroomApi}/content/${contentId}`)
      .pipe(catchError(this.handleError));
  }

  createContent(form: FormData): Observable<ResponseContent> {
    return this.http.post<ResponseContent>(
      `${environment.urlClassroomApi}/content`, 
      form
    ).pipe(catchError(this.handleError));
  }

  deleteContent(contentId: number): Observable<String> {
    return this.http.delete<String>(`${environment.urlClassroomApi}/content/${contentId}`)
      .pipe(catchError(this.handleError));
  }

  updateContent(contentId: number, form:FormData): Observable<String> {
    return this.http.put<String>(`${environment.urlClassroomApi}/content/${contentId}`, form)
      .pipe(catchError(this.handleError));
  }

  //Get an specific content
  getContentByKey(key : string): Observable<any>{
    return this.http.get<any>(environment.urlClassroomApi + "/searchContent?filter=" + key)
      .pipe(
        catchError(this.handleError)
      )
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
