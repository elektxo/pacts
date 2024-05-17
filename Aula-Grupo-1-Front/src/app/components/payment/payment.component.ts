import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IPayPalConfig, ICreateOrderRequest, NgxPayPalModule } from 'ngx-paypal';
import { CourseService } from '../../services/courses/course.service';
import { MessageService } from '../../services/messages/message.service';
import { CourseResponse } from '../../interfaces/CourseResponse';

// add this on details component ts
// import { PaymentComponent } from '../payment/payment.component';
// import { NgxPayPalModule } from 'ngx-paypal';

// add this on details component html
// <app-payment [precio]="course.price.toString()"/>

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [NgxPayPalModule],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  @Input() precio?: String;
  @Input() identificador?: number;
  @Output() paymentCompleted = new EventEmitter<boolean>();
  public paypalConfig?: IPayPalConfig;

  allCourses: CourseResponse[] = [];
  studentCourses: CourseResponse[] = [];

  constructor(private courseService: CourseService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.initConfig();
  }

  private initConfig(): void {
    this.paypalConfig = {
      currency: 'EUR',
      clientId: 'AWbix-wPSBlbPs2CjNDR0SnpWn99ZxjnoHQXINNkTRy9exhvMR4IwAHJXBAaRe62Vm-sOKmhZAaTNdvw',
      createOrderOnClient: (data) =>
        <ICreateOrderRequest>{
          intent: 'CAPTURE',
          purchase_units: [{
            amount: {
              currency_code: 'EUR',
              value: this.precio,
              breakdown: {
                item_total: {
                  currency_code: 'EUR',
                  value: this.precio
                }
              }
            },
            items: [{
              name: 'Enterprise Subscription',
              quantity: '1',
              category: 'DIGITAL_GOODS',
              unit_amount: {
                currency_code: 'EUR',
                value: this.precio
              }
            }]
          }]
        },
      advanced: {
        commit: 'true'
      },
      style: {
        label: 'paypal',
        layout: 'vertical'
      },
      onApprove: (data, actions) => {
        console.log('onApprove - transaction was approved, but not authorized', data, actions);
        actions.order.get().then((details: any) => {
          console.log('onApprove - you can get full order details inside onApprove: ', details);
        });
      },
      onClientAuthorization: (data) => {
        console.log('onClientAuthorization - you should probably inform your server about completed transaction at this point', data);
        this.addStudentToCourse(this.identificador);
        this.paymentCompleted.emit(true);
        return 'Authorized';
      },
      onCancel: (data, actions) => {
        console.log('OnCancel', data, actions);
        return 'Canceled';
      },
      onError: err => {
        console.log('OnError', err);
        return 'Canceled';
      },
      onClick: (data, actions) => {
        console.log('onClick', data, actions);
        return 'Canceled';
      }
    }
  }

  getAllCourses() {
    this.courseService.getAllCourses().subscribe(data => {
      this.allCourses = data;
    })
  }

  getAllCoursesFromStudent() {
    this.courseService.getCoursesByStudent().subscribe({
      next: (courses) => {
        this.studentCourses = courses;
        console.log(this.studentCourses);
      },
      error: (error) => {
        if (error.status === 404) {
          console.log('No se encontraron cursos para el estudiante');
          this.studentCourses = [];
        }
        console.error('Error al obtener los cursos del estudiante:', error);
      }
    })
  }

  addStudentToCourse(courseId: number | undefined) {
    this.courseService.addStudentToCourse(courseId).subscribe({
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
    });
  }
}
