import { LoginService } from './login.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private loginService:LoginService,private router:Router){

  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    
    console.log('AdminGuard - Verificando acceso admin...');
    
    // Verificar si estamos en el navegador
    if (typeof window === 'undefined') {
      console.log('AdminGuard - SSR detectado, permitiendo acceso temporal');
      return true;
    }
    
    const isLoggedIn = this.loginService.isLoggedIn();
    const userRole = this.loginService.getUserRole();
    
    console.log('AdminGuard - Usuario logueado:', isLoggedIn);
    console.log('AdminGuard - Role del usuario:', userRole);
    
    if(isLoggedIn && userRole === 'ADMIN'){
      console.log('AdminGuard - ✅ Acceso permitido');
      return true;
    }

    console.log('AdminGuard - ❌ Acceso denegado, redirigiendo a login');
    this.router.navigate(['login']);
    return false;
  }

}
