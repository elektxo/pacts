import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CourseInterface } from '../../interfaces/Course.interface';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private coursesUrl = "http://localhost:8081/api/v1/courses"

  constructor(private http: HttpClient, private authService: AuthService) { }


  FormCreateCourse : CourseInterface = {
    title: '',
    description: '',
    price: 0,
    free: true,
    teacherId: '',
    contents: []
  }

  //Create a course
  createCourse(form : CourseInterface): Observable<CourseInterface>{
    return this.http.post<CourseInterface>(this.coursesUrl, form)
  }

  //Get all courses
  getAllCourses(): Observable<CourseInterface[]>{
    return this.http.get<CourseInterface[]>(this.coursesUrl)
    
  }

  //Get an specific course by id
  getOneCourse(id : any): Observable<CourseInterface>{
    return this.http.get<CourseInterface>(`${this.coursesUrl}/${id}`)
  }

  //Delete an specific course by id
  deleteCourse(id: any){
    return this.http.delete(`${this.coursesUrl}/${id}`)
  }

}
