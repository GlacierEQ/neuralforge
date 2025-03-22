import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

export const accessTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  let headers = {};

  // If user is not authenticated, proceed without modifying the request
  if (!authService.check()) return next(req);

  // Add Authorization header to non-auth requests
  if (!req.url.includes('auth')) {
    headers = {
      setHeaders: {
        Authorization: `Bearer ${authService.getAccessToken()?.replace(/"/g, '')}`,
      },
    };
  }

  const clonedRequest = req.clone(headers);

  return next(clonedRequest).pipe(
      catchError((error: HttpErrorResponse): Observable<never> => {
        if (error.status === 401) { // Unauthorized
          authService.logout(); // Remove token (assuming logout() handles this)
          window.location.reload(); // Reload application
        }
        return throwError(() => error);
      })
  );
};
