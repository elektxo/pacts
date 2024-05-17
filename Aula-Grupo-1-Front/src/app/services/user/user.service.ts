import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthService } from '../auth/auth.service';
import { User } from '../../interfaces/User';
import { UserRequest } from '../../interfaces/UserRequest';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  user: User | null = this.authService.getProfile()

  updateProfile(Form : UserRequest): Observable<User> {
    return this.http.put<User>(`${environment.urlClassroomApi}/users/${this.user?.id}`, Form)
  }
}