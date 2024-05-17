import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CourseService } from '../../services/courses/course.service';
import { ActivatedRoute } from '@angular/router';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { ContentService } from '../../services/content/content.service';
import { User } from '../../interfaces/User';
import { ResponseContent } from '../../interfaces/ResponseContent';
import { AuthService } from '../../services/auth/auth.service';
import { forkJoin } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageComponent } from '../messages/message/message.component';
import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [MessageComponent, SearchComponent],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.css'
})
export class CourseDetailsComponent implements OnInit {

  course!: CourseResponse;
  user!: User | null;
  teacherContents!: ResponseContent[];
  filteredContents: ResponseContent[] = [];
  errorMessage!: string;
  searchTerm: string = '';
  url:string = environment.urlClassroomApi + "/contents/image/";

  constructor(private courseService: CourseService, private route: ActivatedRoute, private contentService: ContentService, private tokenService: AuthService) { }

  ngOnInit(): void {
    this.getCourseAndContents();
  } 

  getCourseAndContents(){
    forkJoin([this.courseService.getCourseById(this.route.snapshot.params['id']), this.contentService.getTeacherContents(this.tokenService.getProfile()!.id),  ]).subscribe(result => {

      this.course = result[0];
      console.log(this.course);
      
      this.teacherContents = result[1];
      console.log(this.teacherContents);
      
      this.getContentsFiltered();
      console.log(this.filteredContents);
      
    });
  }

  //Get an specific course
  getCourse(courseId: number) {
    this.courseService.getCourseById(courseId).subscribe(course => {
      this.course = course;
      console.log(this.course);
    })
  }

  //Get all contents from a teacher
  getTeacherContents() {
    if (this.user != null) {
      this.contentService.getTeacherContents(this.user.id).subscribe({
        next: (contents) => {
          this.teacherContents = contents;
        },
        error: (error: HttpErrorResponse) => {
          console.log(error.status); // Now status should be correctly logged
          if (error.status === 404) {
            this.errorMessage = "You don't have any contents yet. Please add some.";
          } else {
            this.errorMessage = 'An error occurred: ' + error.message;
          }
          console.error(this.errorMessage);
        }
      });
    }
  }

  getContentsFiltered = () => {

    this.filteredContents = [];

    this.teacherContents.forEach(tcontent => {
      let isIncluded : boolean = false;
      this.course.contents!.forEach(ccontent => {
        if(ccontent.id == tcontent.id){
          isIncluded = true;
        }
      });
      if(!isIncluded){
        console.log(tcontent);
        console.log(this.filteredContents);
        this.filteredContents.push(tcontent)
      }
    });
  }

  //Add content to a course
  addContentToCourse(contentId: number){
    this.courseService.addContentToCourse(this.course.id!, contentId).subscribe(
      {
        next: () => {
          this.getCourseAndContents();
        },
        error: (error) => {
          this.getCourseAndContents();
        }
      }
    );
  }

  //Remove contents from a course
  removeContentFromCourse(contentId: number) {
    this.courseService.removeContentFromCourse(this.course.id!, contentId).subscribe(
      {
        next: () => {
          this.getCourseAndContents();
        },
        error: (error) => {
          this.getCourseAndContents();
        }
      }
    );
  }

  contentByKey(key : string){
    this.contentService.getContentByKey(key).subscribe({
      next: (content) => {
        if((typeof content) == "string"){
          this.course.contents = [];
        }else{
          this.course.contents= content;
        }
      },
      error: (error: HttpErrorResponse) => {
        console.log(error.status); 
        if (error.status === 404) {
          console.log(error.status)
          this.errorMessage = "Not found contents";
          this.teacherContents = [];
        } else {
          this.errorMessage = 'An error occurred: ' + error.message;
        }
      }
    })
  }
}
