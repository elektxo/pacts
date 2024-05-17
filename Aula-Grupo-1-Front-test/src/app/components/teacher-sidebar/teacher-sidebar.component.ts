import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-teacher-sidebar',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  templateUrl: './teacher-sidebar.component.html',
  styleUrl: './teacher-sidebar.component.css'
})
export class TeacherSidebarComponent {

}
