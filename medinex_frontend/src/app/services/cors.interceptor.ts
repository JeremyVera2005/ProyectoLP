import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class CorsInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Clone the request and add CORS headers
    const corsRequest = req.clone({
      setHeaders: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
      }
    });

    return next.handle(corsRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('HTTP Error:', error);
        return throwError(error);
      })
    );
  }
}
