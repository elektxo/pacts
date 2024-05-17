import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { StudentDashboardComponent } from "./components/student-dashboard/student-dashboard.component";
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';

import { UserProfileComponent } from "./components/user-profile/user-profile.component";
import { ExplorerComponent } from './components/explorer/explorer.component';
import { TeacherDashboardComponent } from './components/teacher-dashboard/teacher-dashboard.component';
import { MessageComponent } from './components/messages/message/message.component';
import { StudentCourseComponent } from "./components/student-course/student-course.component";
@Component({
    selector: 'app-root',
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    imports: [
        RouterOutlet,
        NavbarComponent,
        FooterComponent,
        FormsModule,
        UserProfileComponent,
        StudentDashboardComponent,
        TeacherDashboardComponent,
        ExplorerComponent,
        MessageComponent,
        StudentCourseComponent
    ]
})
export class AppComponent {
  title = 'Aula-Grupo-1-Front';
}
