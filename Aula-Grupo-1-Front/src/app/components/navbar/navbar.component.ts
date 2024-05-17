import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  isUserLogged: boolean = false
  username: string = ""
  roles: string[] = []
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe({
      next: (logged) => {
        this.isUserLogged = logged;
        this.username = this.authService.getUsername()
        this.roles = this.authService.getRoles()
      }
    })
  }

  login() {
    this.authService.login()
  }

  logout() {
    this.authService.logout()
  }
}
