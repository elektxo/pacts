import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { CourseFormComponent } from '../course-form/course-form.component';
import { ContentFormComponent } from './content-form/content-form.component';
import { RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-teacher-dashboard',
  standalone: true,
  imports: [FormsModule, CommonModule, CourseFormComponent, ContentFormComponent, RouterOutlet],
  providers:[HttpClient],
  templateUrl: './teacher-dashboard.component.html',
  styleUrl: './teacher-dashboard.component.css'
})
export class TeacherDashboardComponent{
  constructor(){}
}
