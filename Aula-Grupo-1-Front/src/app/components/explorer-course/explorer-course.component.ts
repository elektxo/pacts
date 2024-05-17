import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ResponseContent } from '../../interfaces/ResponseContent';
import { User } from '../../interfaces/User';
import { AuthService } from '../../services/auth/auth.service';
import { ContentService } from '../../services/content/content.service';
import { CourseService } from '../../services/courses/course.service';
import { PaymentComponent } from '../payment/payment.component';
import { NgxPayPalModule } from 'ngx-paypal';
import { MessageService } from '../../services/messages/message.service';
import { CommonModule } from '@angular/common';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { environment } from '../../../environments/environment.development';

@Component({
  selector: 'app-explorer-course',
  standalone: true,
  imports: [PaymentComponent, NgxPayPalModule, CommonModule],
  templateUrl: './explorer-course.component.html',
  styleUrl: './explorer-course.component.css'
})
export class ExplorerCourseComponent implements OnInit {
  isSuscribed!: boolean;
  course!: CourseResponse;
  courseContents!: ResponseContent[];
  errorMessage!: string;
  allCourses: CourseResponse[] = [];
  studentCourses: CourseResponse[] = [];
  imageLink: string = environment.urlClassroomApi + '/courses/image/';

  constructor(private courseService: CourseService, private route: ActivatedRoute, private authService: AuthService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.getCourse();
  }

  getCourse() {
    this.courseService.getCourseById(this.route.snapshot.params['id']).subscribe({
      next: (course) => {
        this.course = course;
        this.courseContents = course.contents!;
        console.log(this.courseContents);
      },
      error: (error: HttpErrorResponse) => {
        console.log(error.status);
        if (error.status === 404) {
          this.errorMessage = "Course not found";
        } else {
          this.errorMessage = 'An error occurred: ' + error.message;
        }
        console.error(this.errorMessage);
      },
      complete: () => {
        this.getAllCoursesFromStudent();
      }
    });
  }

  getAllCoursesFromStudent() {
    this.courseService.getCoursesByStudent().subscribe({
      next: (courses) => {
        this.studentCourses = courses;
        console.log(this.studentCourses);
      },
      error: (error) => {
        if (error.status === 404) {
          console.log('No se encontraron cursos para el estudiante');
          this.studentCourses = [];
        }
        console.error('Error al obtener los cursos del estudiante:', error);
      },
      complete: () => {
        this.isSuscribed = this.isSubscribed(this.course);
      }
    })
  }

  addStudentToCourse(courseId: number) {
    console.log('Agregando estudiante al curso', courseId)
    this.courseService.addStudentToCourse(courseId).subscribe({
      next: (resp) => {
        console.log('Estudiante agregado exitosamente al curso');
        this.messageService.sendMessage('Course added successfully.');
      },
      error: (error) => {
        console.error('Error al agregar estudiante al curso:', error);
        this.messageService.sendMessage('You are already subscribed to this course.');
      },
      complete: () => {
        this.getCourse();
      }
    });
  }

  isSubscribed(course: CourseResponse): boolean {
    return this.studentCourses.some(studentCourse => studentCourse.id === course.id);
  }

  onPaymentCompleted(success: boolean) {
    if (success) {
      this.isSuscribed = true;
      this.getCourse();
    }
  }
}

