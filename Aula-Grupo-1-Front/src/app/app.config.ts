import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideOAuthClient } from 'angular-oauth2-oidc';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { jwtInterceptorInterceptor } from './interceptors/jwt/jwt.interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), 
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptors([jwtInterceptorInterceptor])),
    provideOAuthClient()
  ]
};
