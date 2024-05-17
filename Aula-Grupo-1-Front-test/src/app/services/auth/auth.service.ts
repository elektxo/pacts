import { Injectable, inject } from '@angular/core';
import { AuthConfig, OAuthEvent, OAuthService } from 'angular-oauth2-oidc';
import { BehaviorSubject, Observable} from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { User } from '../../interfaces/User';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private oAuthService = inject(OAuthService)

  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor() {
    this.initConfiguration()
    this.updateLoggedInState()
  }

  // Start configuration oauth keycloak
  initConfiguration() {
    const authConfig: AuthConfig = {
      issuer: environment.auth.issuer,
      clientId: environment.auth.clientId,
      tokenEndpoint: `${environment.auth.issuer}/protocol/openid-connect/token`,
      responseType: 'code',
      redirectUri: "http://localhost:4200",
      scope: "openid profile email",
      showDebugInformation: true,
    }

    this.oAuthService.configure(authConfig);
    this.oAuthService.setupAutomaticSilentRefresh();
    this.oAuthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      this.updateLoggedInStateOnLoad();
    });
  }

  login() {
    this.oAuthService.initCodeFlow();
  }

  logout() {
    this.oAuthService.revokeTokenAndLogout();
  }

  getProfile(): User | null {
    const claims = this.oAuthService.getIdentityClaims();
    if (!claims) {
      return null;
    }
    return {
      id: claims['sub'],
      name: claims['name'],
      username: claims['preferred_username'],
      firstname: claims['given_name'],
      lastname: claims['family_name'],
      email: claims['email'],
      email_verified: claims['email_verified'],
    };
  }

  getUsername() {
    if (this.isLoggedInSubject.value) {
      const claims = this.oAuthService.getIdentityClaims();
      return claims['preferred_username'];
    }
  }

  getLastname(): string {
    return this.oAuthService.getIdentityClaims()["family_name"];;
  }

  getEmail(): string {
    if (this.isLoggedInSubject.value) {
      return this.oAuthService.getIdentityClaims()["email"];
    }
    else {
      return "";
    }
  }

  getToken() {
    return this.oAuthService.getAccessToken();
  }

  getRoles(): string[] {
    const token = this.oAuthService.getAccessToken();
    if (!token) return [];

    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('JWT token is not correctly formed.');
    }

    try {
      const decodedPayload = atob(parts[1]);
      const payloadObj = JSON.parse(decodedPayload);
      const roles = payloadObj.resource_access['virtual-classroom-rest-api'].roles as string[];
      return roles;
    } catch (error) {
      console.error('Failed to decode token:', error);
      return [];
    }
  }
  

  private updateLoggedInState() {
    this.oAuthService.events.subscribe((event: OAuthEvent) => {
      if (event.type === 'token_received' || event.type === 'token_refreshed') {
        this.isLoggedInSubject.next(this.oAuthService.hasValidAccessToken());
      } else if (event.type === 'logout') {
        this.isLoggedInSubject.next(false);
      }
    });
  }

  private updateLoggedInStateOnLoad() {
    this.isLoggedInSubject.next(this.oAuthService.hasValidAccessToken());
  }
}