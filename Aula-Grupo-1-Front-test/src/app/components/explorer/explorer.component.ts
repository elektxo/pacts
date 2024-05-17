import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchComponent } from '../search/search.component';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { CourseService } from '../../services/courses/course.service';
import { AuthService } from '../../services/auth/auth.service';
import { MessageService } from '../../services/messages/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';

@Component({
    selector: 'app-explorer',
    standalone: true,
    templateUrl: './explorer.component.html',
    styleUrl: './explorer.component.css',
    imports: [CommonModule, SearchComponent]
})
export class ExplorerComponent {
[x: string]: any;
  allCourses: CourseResponse[] = [];
  studentCourses: CourseResponse[] = [];
  filteredCourses: CourseResponse[] = [];
  searchTerm: string = '';
  roles!: string[];
  errorMessage!: string;
  url: string = environment.urlClassroomApi+'/courses/image/';

  constructor ( private courseService: CourseService, private messageService:MessageService, private authService:AuthService) {}

  ngOnInit(): void{
    this.getAllCourses();
    this.getAllCoursesFromStudent();
    this.roles = this.authService.getRoles()
  }

  @Output() searchEvent = new EventEmitter<string>();
  search() {
    this.searchEvent.emit(this.searchTerm);
  }

  getAllCourses(){
    this.courseService.getAllCourses().subscribe( data => {
      this.allCourses = data;
    })
  }

  getAllCoursesFromStudent(){
    this.courseService.getCoursesByStudent().subscribe({
      next: (courses) => {
        this.studentCourses = courses;
        console.log(this.studentCourses);
      },
      error: (error) => {
        if(error.status === 404){
          console.log('No se encontraron cursos para el estudiante');
          this.studentCourses = [];
        }
        console.error('Error al obtener los cursos del estudiante:', error);
      }
    })
  }

  cursoByKey(key:string){
    this.courseService.getDataByKey(key).subscribe({ 
      next: (data) => {
      if ((typeof data) == "string"){
        this.allCourses = [];
      }else{
        this.allCourses = data;
        console.log(this.allCourses);
      }
      console.log(typeof data);
    },
    error: (error: HttpErrorResponse) => {
      console.log(error.status); 
      if (error.status === 404) {
        console.log(error.status)
        this.errorMessage = "Not found courses";
        this.allCourses = [];
      } else {
        this.errorMessage = 'An error occurred: ' + error.message;
      }
    }
  })
    console.log('Búsqueda:', key);

  }

  addStudentToCourse(courseId: number) {
    this.courseService.addStudentToCourse(courseId).subscribe(
      {
        next: (resp) => {
          console.log('Estudiante agregado exitosamente al curso');
          this.getAllCourses();
          this.getAllCoursesFromStudent();
          this.messageService.sendMessage('Course added successfully.');
        },
        error: (error) => {
          console.error('Error al agregar estudiante al curso:', error);
          this.messageService.sendMessage('You are already subscribed to this course.');
        }
      }
    );
  }

  removeStudentFromCourse(courseId: number) {
    this.courseService.removeStudentFromCourse(courseId).subscribe(
      {
        next: (resp) => {
          console.log('Estudiante eliminado exitosamente al curso');
          this.getAllCourses();
          this.getAllCoursesFromStudent();
          this.messageService.sendMessage('Course removed successfully.');
        },
        error: (error) => {
          console.error('Error al eliminado estudiante al curso:', error);
        }
      }
    );
  }

  isSubscribed(course: CourseResponse): boolean {
    return this.studentCourses.some(studentCourse => studentCourse.id === course.id);
}

}
