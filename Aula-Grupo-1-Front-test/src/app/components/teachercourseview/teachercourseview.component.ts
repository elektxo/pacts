import { Component, OnInit } from '@angular/core';
import { User } from '../../interfaces/User';
import { CourseService } from '../../services/courses/course.service';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { CourseRequest } from '../../interfaces/CourseRequest';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { environment } from '../../../environments/environment.development';

@Component({
  selector: 'app-teachercourseview',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './teachercourseview.component.html',
  styleUrl: './teachercourseview.component.css'
})
export class TeachercourseviewComponent implements OnInit{

  //On init, gets the profile of the user via token
  ngOnInit(): void {
    this.user = this.tokenService.getProfile();
    this.getAllTeacherCourses();
  }

  teacherCourses : CourseResponse [] = []; 

  user!: User | null
  courseToDelete!: CourseResponse;
  url:string = environment.urlClassroomApi + "/courses/image/";

  constructor(
    private courseService: CourseService,
    private tokenService: AuthService,
    private router: Router,
    ){}

  //Get all courses from the teacher
  getAllTeacherCourses(){
    this.courseService.getAllTeacherCourses(this.user!.id).subscribe(teacherCourses => {
      this.teacherCourses = teacherCourses;
      console.log(this.teacherCourses)
    })
  }

  toCourseDetail(courseId: number){
    this.router.navigateByUrl(`/sidebar-teacher/course-details/${courseId}`);
  }

  toCourseEdit(courseId: number){
    this.router.navigateByUrl(`/sidebar-teacher/course-form/${courseId}`);
  }

  //Open a modal
  openDeleteCourseWindow(courseToDelete: CourseResponse){
    const deleteModal = document.getElementById("deleteModal");

      if (deleteModal != null) {
        deleteModal.style.display = "block";
        this.courseToDelete = courseToDelete;
      }
  }

  closeDeleteCourseWindow(){
    const deleteModal = document.getElementById("deleteModal");

    if(deleteModal != null){
      deleteModal.style.display = "none"
    }
  }

  //Delete the course
  deleteCourse( courseId: number){
    const deleteModal = document.getElementById("deleteModal");
    
    this.courseService.deleteCourse(courseId).subscribe(resp => {
      console.log(resp);
      this.getAllTeacherCourses();
    })
    if(deleteModal != null){
      deleteModal.style.display = "none"
    }

  }

}
