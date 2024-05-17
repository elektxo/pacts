import { Component, NgModule, inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { User } from '../../interfaces/User';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user/user.service';
import { UserRequest } from '../../interfaces/UserRequest';
import { MessageService } from '../../services/messages/message.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
  
  authService = inject(AuthService);
  formBuilder = inject(FormBuilder);
  userService = inject(UserService);
  messageService = inject(MessageService)
  user: User | null = this.authService.getProfile()

  isUserLogged: boolean = false
  username!: string;
  lastname!: string;
  email: string = "";

  editProfileForm = this.formBuilder.group({
    firstname : ['', [Validators.required, Validators.maxLength(15)]],
    lastname: ['', [Validators.required, Validators.maxLength(15)]]
  })

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(logged => {
      this.isUserLogged = logged;
      if (logged) {
        this.updateProfileInfo();
      }
    });
  }

  get firstName() {
    return this.editProfileForm.controls.firstname
  }

  get lastName() {
    return this.editProfileForm.controls.lastname
  }

  updateProfile() {
    this.userService.updateProfile(this.editProfileForm.value as UserRequest).subscribe({
      error: (errorData) => {
        this.messageService.sendMessage("Ups! parece que hubo un error al actualizar el perfil")
      },
      complete: () => {
        this.messageService.sendMessage(`Actualizado`)
        this.authService.logout()
      }
    })
  }
  
  updateProfileInfo() {
    this.username = this.authService.getUsername();
    this.lastname = this.authService.getLastname();
    this.email = this.authService.getEmail();
  }
  
}

