import { LoginService } from './login.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree , Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NormalGuard implements CanActivate {

  constructor(private loginService:LoginService,private router:Router){

  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      console.log('NormalGuard - Verificando acceso');
      console.log('NormalGuard - isLoggedIn():', this.loginService.isLoggedIn());
      console.log('NormalGuard - getUserRole():', this.loginService.getUserRole());
      
      if(this.loginService.isLoggedIn() && this.loginService.getUserRole() == 'NORMAL'){
        console.log('NormalGuard - Acceso permitido');
        return true;
      }

      console.log('NormalGuard - Acceso denegado, redirigiendo a login');
      this.router.navigate(['login']);
      return false;
  }

}
