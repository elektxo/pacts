import { Component } from '@angular/core';
import { CourseService } from '../../services/courses/course.service';
import { AuthService } from '../../services/auth/auth.service';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { environment } from '../../../environments/environment.development';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from '../../services/messages/message.service';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './student-dashboard.component.html',
  styleUrl: './student-dashboard.component.css'
})
export class StudentDashboardComponent {

  studentUsername: string = "";
  studentCourses: CourseResponse[] = [];
  url: string = environment.urlClassroomApi + "/courses/image/";
  errorMessage!: string

  constructor(private courseService: CourseService, 
    private authService:AuthService, 
    private messageService:MessageService,
    private router: Router) { }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe({
      next: (logged) => {
        this.studentUsername = this.authService.getUsername()
      }
    })
    this.loadStudentCourses();
      
  }

  loadStudentCourses(): void {
    this.courseService.getCoursesByStudent().subscribe({
      next: (courses) => {
        this.studentCourses = courses;
        console.log(this.authService.getUsername())
        console.log(this.studentCourses);
      },
      error: (error: HttpErrorResponse) => {
        console.log(error.status); 
        if (error.status === 404) {
          console.log(error.status)
          this.errorMessage = "You don't have any course subscription yet. Please add some.";
        } else {
          this.errorMessage = 'An error occurred: ' + error.message;
        }
      }
    });
  }

  removeStudentFromCourse(courseId: number) {
    this.courseService.removeStudentFromCourse(courseId).subscribe(
      response => {
        console.log('Estudiante eliminado exitosamente al curso');
        this.messageService.sendMessage('Unsubscribed successfully');
        this.loadStudentCourses();
      },
      error => {
        console.error('Error al eliminado estudiante al curso:', error);
        this.messageService.sendMessage('Error: unable to unsubscribe. Please, try again.');
      }
    );
  }

  toCourseDetail(courseId: number) {
    console.log(`Navigating to course details for ID: ${courseId}`);
    this.router.navigateByUrl(`/sidebar-student/student-courses/${courseId}`).then(success => {
        console.log('Navigation success:', success);
    }, error => {
        console.log('Navigation error:', error);
    });
}

}

