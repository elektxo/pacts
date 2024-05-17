import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { ResponseContent } from '../../interfaces/ResponseContent';
import { AuthService } from '../../services/auth/auth.service';
import { ContentService } from '../../services/content/content.service';
import { CourseService } from '../../services/courses/course.service';
import { CommentsSectionComponent } from '../comments-section/comments-section.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-student-course',
  standalone: true,
  imports: [CommentsSectionComponent, ReactiveFormsModule],
  templateUrl: './student-course.component.html',
  styleUrl: './student-course.component.css'
})
export class StudentCourseComponent implements OnInit {
  course!: CourseResponse;
  courseContents!: ResponseContent[];
  errorMessage!: string;
  totalHours!: number;
  completedHours!: number;
  progressCompleted: number = 0;

  constructor(
    private courseService: CourseService, 
    private route: ActivatedRoute, 
    private contentService: ContentService, 
    private authService: AuthService, 
    private formBuilder: FormBuilder
  ) { }

  completedForm = this.formBuilder.group({
    completed: [''],
  });

  ngOnInit(): void {
    this.getCourse();
  }

  getCourse() {
    this.courseService.getCourseByStudent(this.route.snapshot.params['id']).subscribe({
      next: (course) => {
        this.course = course;
        this.courseContents = course.contents!;
        this.totalHours = this.getTotalHours();
        this.completedHours = this.getHoursCompleted();
        if(course.contents?.length !== 0) {
          this.progressCompleted = this.calculateProgress();
        }
      },
      error: (error: HttpErrorResponse) => {
        console.log(error.status);
        if (error.status === 404) {
          this.errorMessage = "Course not found";
        } else {
          this.errorMessage = 'An error occurred: ' + error.message;
        }
        console.error(this.errorMessage);
      }
    });
  }

  getTotalHours(): number {
    let totalHours = 0;
    this.courseContents.forEach(content => {
      totalHours += content.estimatedHours!;
    });
    return totalHours;
  }

  getHoursCompleted(): number {
    let hoursCompleted = 0;
    this.courseContents.forEach(content => {
      if (content.completed) {
        hoursCompleted += content.estimatedHours!;
      }
    });
    return hoursCompleted;
  }

  toggleCompleteContent(contentId: number, event: Event) {
    const checkbox = event.target as HTMLInputElement;
    const completed = checkbox.checked;
    this.courseService.updateCompleteContent(this.course.id!, contentId, completed).subscribe({
      next: (message) => {
        this.getCourse();
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          this.errorMessage = "Course not found";
        } else {
          this.errorMessage = 'An error occurred: ' + error.message;
        }
      }
    });
  }

  calculateProgress(): number {
    return (this.completedHours / this.totalHours) * 100;
  }
}
