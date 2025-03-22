import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

/**
 * HTTP Interceptor for attaching an access token to API requests.
 *
 * This interceptor checks if the user is authenticated and appends the
 * Authorization header with the Bearer token for requests that do not
 * include 'auth' in the URL.
 *
 * @param req - The outgoing HTTP request.
 * @param next - The next handler in the request pipeline.
 * @returns The modified HTTP request with authentication headers if applicable.
 */
export const accessTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  let headers = {};

  // If user is not authenticated, proceed without modifying the request
  if (!authService.check()) return next(req);

  // Add the Authorization header to requests that do not include 'auth' in the URL.
  headers = {
    setHeaders: {
      Authorization: `Bearer ${authService.getAccessToken()}`,
    },
  };

  // Clone the request with the modified headers.
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