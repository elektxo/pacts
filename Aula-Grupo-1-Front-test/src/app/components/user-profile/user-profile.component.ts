import { Component, NgModule, inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { User } from '../../interfaces/User';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
  
  authService = inject(AuthService);
  user: User | null = this.authService.getProfile()

  isUserLogged: boolean = false
  username: string = "";
  lastname: string = "";
  email: string = "";

  editing = false;
  personService: any;


  editProfile() {
    this.editing = !this.editing;
    console.log('Editing profile');
  }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(logged => {
      this.isUserLogged = logged;
      if (logged) {
        this.updateUserProfile();
      }
    });
  }
  
  updateUserProfile() {
    this.username = this.authService.getUsername();
    this.lastname = this.authService.getLastname();
    this.email = this.authService.getEmail();
  }
  
}

