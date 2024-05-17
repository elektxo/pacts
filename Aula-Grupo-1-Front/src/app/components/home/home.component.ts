import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseResponse } from '../../interfaces/CourseResponse';
import { environment } from '../../../environments/environment.development';
import { CourseService } from '../../services/courses/course.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  allCourses: CourseResponse[] = [];
  url: string = environment.urlClassroomApi+'/courses/image/';

  
  collaborators = [
    { name: 'Oracle', imgSrc: 'https://cdn-www.infobip.com/wp-content/uploads/2020/10/14135942/oracle-logo.png' },
    { name: 'Microsoft', imgSrc: 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/Microsoft_logo_%282012%29.svg/1024px-Microsoft_logo_%282012%29.svg.png' },
    { name: 'PostgreSQL', imgSrc: 'https://programacion.net/files/article/20151129021117_postgresql-logo.png' },
    { name: 'Spring', imgSrc: 'https://media.licdn.com/dms/image/D4D12AQFscCu_T0xB3A/article-cover_image-shrink_600_2000/0/1688794846091?e=2147483647&v=beta&t=W6FiJ_ZGjTh79I8xtLZZ8_-zu58OQb-fcB3cNbc2dZw' },
    { name: 'Eviden', imgSrc: 'https://www.etp4hpc.eu/image/fotos/logotype_eviden_rgb_orange.png' }
  ];

  testimonials = [
    {
      text: 'This course was a game-changer for my career. The hands-on approach and expert guidance by the instructor made learning complex concepts easy.',
      author: 'Jane Doe',
      position: 'Senior Developer',
      imgSrc: 'https://thumbs.dreamstime.com/b/l%C3%ADnea-icono-del-negro-avatar-perfil-de-usuario-121102131.jpg'
    },
    // Puedes agregar más testimonios aquí
  ];

  randomTestimonial = this.testimonials[Math.floor(Math.random() * this.testimonials.length)];

  constructor(private courseService:CourseService,private router:Router) { }

  ngOnInit(): void {
    this.getAllCourses();
  }

  getAllCourses(){
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        this.allCourses = courses;
        console.log(this.allCourses)
      },
      error: (error) => {
        console.error('Error al obtener los cursos:', error);
      }
    })
  }
}
