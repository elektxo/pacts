import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CourseService } from '../../services/courses/course.service';
import { AuthService } from '../../services/auth/auth.service';
import { CourseRequest } from '../../interfaces/CourseRequest';
import { ActivatedRoute, Router } from '@angular/router';
import { first } from 'rxjs';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { response } from 'express';
import { MessageService } from '../../services/messages/message.service';

@Component({
  selector: 'app-course-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './course-form.component.html',
  styleUrl: './course-form.component.css'
})
export class CourseFormComponent implements OnInit {

  id!: number;
  isCreate!: boolean;
  course!: CourseResponse;
  selectedFile: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private courseService: CourseService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ) {}

  courseForm = this.formBuilder.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    free: [false, Validators.required],
    teacherId: ['']
  });

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.isCreate = !this.id;
    console.log(this.id, this.isCreate);

    if (!this.isCreate) {
      this.courseService.getCourseById(this.id)
        .pipe(first())
        .subscribe(resp => {
          this.course = resp;
          this.courseForm.patchValue(resp);
        });
    }
  }

  saveCourse() {
    if (this.courseForm.invalid) {
      return;
    }

    if (this.isCreate) {
      this.createCourse();
    } else {
      this.updateCourse();
    }
  }

  get title() {
    return this.courseForm.controls.title;
  }

  get description() {
    return this.courseForm.controls.description;
  }

  get price() {
    return this.courseForm.controls.price;
  }

  get free() {
    return this.courseForm.controls.free;
  }

  createCourse() {
    const formCourse = new FormData();

    let course: CourseRequest = {
      title: this.courseForm.value.title!,
      description: this.courseForm.value.description!,
      price: this.courseForm.value.price!,
      free: this.courseForm.value.free!,
      teacherId: this.authService.getProfile()!.id
    };

    formCourse.append("course", new Blob([JSON.stringify(course)], {type: 'application/json'}));
    if (this.selectedFile) {
      formCourse.append("image", this.selectedFile);
    }

    this.courseService.createCourse(formCourse)
      .pipe(first())
      .subscribe({
        next: response => {
          this.router.navigateByUrl('sidebar-teacher/teacher-courses');
          this.messageService.sendMessage('Course created successfully');
        },
        error: error => {
          console.log(error);
          this.router.navigateByUrl('sidebar-teacher/teacher-courses');
          this.messageService.sendMessage('Unable to CREATE course. Please, try again.');
        }
      });
  }

  updateCourse() {
    const formCourse = new FormData();
    let course: CourseRequest = {
      title: this.courseForm.value.title!,
      description: this.courseForm.value.description!,
      price: this.courseForm.value.price!,
      free: this.courseForm.value.free!,
      teacherId: this.authService.getProfile()!.id
    };
    formCourse.append("course", new Blob([JSON.stringify(course)], {type: 'application/json'}));

    if (this.selectedFile) {
      formCourse.append("image", this.selectedFile);
    } else {
      // If no image is selected, send an empty file
      formCourse.append("image", new File([""], "empty"));
    }

    this.courseService.updateCourse(this.id, formCourse)
      .pipe(first())
      .subscribe({
        next: resp => {
          this.router.navigateByUrl('sidebar-teacher/teacher-courses');
          this.messageService.sendMessage('Course updated successfully');
        },
        error: error => {
          this.router.navigateByUrl('sidebar-teacher/teacher-courses');
          this.messageService.sendMessage('Unable to UPDATE course. Please, try again.');
        }
      });
  }

  uploadFile($event: Event) {
    const target = $event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
    } else {
      this.selectedFile = null;
    }
  }
}
