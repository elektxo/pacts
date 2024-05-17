import { Component } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import emailjs from '@emailjs/browser';
import { ContactUsInterface } from '../../interfaces/ContactUs.interface';
import { MessageService } from '../../services/messages/message.service';

@Component({
  selector: 'app-contact-us',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule],
  templateUrl: './contact-us.component.html',
  styleUrl: './contact-us.component.css'
})
export class ContactUsComponent {

  //A form that implements the interface
  FormContact : ContactUsInterface = {
    to_name: '',
    from_name:'',
    from_email: '',
    subject:'',
    message:''
  }


  constructor(private fb: FormBuilder, private messageService:MessageService){}

  //This function sends the email and notify the user
  async send(){

    emailjs.init('hybCyMNuk6b783R4u') //This is a local key provided by the emailjs Service
    await emailjs.send("service_1xarvi5","template_ebfdeey",{
    to_name: this.FormContact.to_name,
    from_name: this.FormContact.from_name,
    from_email: this.FormContact.from_email,
    subject: this.FormContact.subject,
    message: this.FormContact.message,
    });

    //This notify the user that the email has bee sent
    this.messageService.sendMessage('Mail has been sent.');


    //This resets the form
    this.FormContact.from_name = '';
    this.FormContact.from_email = '';
    this.FormContact.subject= '';
    this.FormContact.message = '';
  }



}
