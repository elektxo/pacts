import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';  // Asegúrate de que la ruta de importación sea correcta
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

export const clientUserGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const roles = authService.getRoles();
  if (roles.includes('client_user')) {
    return true;
  } else {
    router.navigate(['/']);
    return false;
  }
};
