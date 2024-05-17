import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptorInterceptor: HttpInterceptorFn = (req, next) => {
  //Get the token from the session-storage
  const token = sessionStorage.getItem('access_token');

  /**
   * ask if there is a token,
   * intercepts it and returns a clone of the request
   * if not, just return the request
   */
  if(token){
    const clonRequest = req.clone({setHeaders: {Authorization: `Bearer ${token}`}});
    return next(clonRequest);
  } else {
    return next(req);
  }

};
