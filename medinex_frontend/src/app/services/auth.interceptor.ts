import { LoginService } from './login.service';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HTTP_INTERCEPTORS } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor{

  constructor(private loginService:LoginService) {

  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq = req;
    const token = this.loginService.getToken();
    
    console.log('AuthInterceptor - URL:', req.url);
    console.log('AuthInterceptor - Token:', token ? 'Token presente' : 'Sin token');
    
    // No agregar token para el endpoint de generación de token
    if(token != null && !req.url.includes('generate-token')){
      console.log('AuthInterceptor - Agregando Bearer token al header');
      authReq = authReq.clone({
        setHeaders : {Authorization: `Bearer ${token}` }
      })
    } else {
      console.log('AuthInterceptor - No se agrega token (sin token o es generate-token)');
    }
    return next.handle(authReq);
  }

}

export const authInterceptorProviders = [
  {
    provide : HTTP_INTERCEPTORS,
    useClass : AuthInterceptor,
    multi : true
  }
]
