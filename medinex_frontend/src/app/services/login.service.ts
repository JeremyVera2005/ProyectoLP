import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  public loginStatusSubjec = new Subject<boolean>();

  constructor(private http:HttpClient) { }

  //generamos el token
  public generateToken(loginData:any){
    console.log('Enviando datos de login:', loginData);
    console.log('URL del endpoint:', `${baserUrl}/generate-token`);
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    return this.http.post(`${baserUrl}/generate-token`, loginData, { headers });
  }

  public getCurrentUser(){
    return this.http.get(`${baserUrl}/actual-usuario`);
  }

  //iniciamos sesión y establecemos el token en el localStorage
  public loginUser(token:any){
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem('token',token);
    }
    return true;
  }

  public isLoggedIn(){
    // Verificar si estamos en el navegador (no en SSR)
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      console.log('LoginService - isLoggedIn() - No hay localStorage (SSR)');
      return false;
    }
    
    let tokenStr = localStorage.getItem('token');
    console.log('LoginService - isLoggedIn() - token:', tokenStr);
    if(tokenStr == undefined || tokenStr == '' || tokenStr == null){
      console.log('LoginService - Usuario NO logueado');
      return false;
    }else{
      console.log('LoginService - Usuario SÍ logueado');
      return true;
    }
  }

  //cerranis sesion y eliminamos el token del localStorage
  public logout(){
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    return true;
  }

  //obtenemos el token
  public getToken(){
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem('token');
  }

  public setUser(user:any){
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem('user', JSON.stringify(user));
    }
  }

  public getUser(){
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      console.log('LoginService - getUser() - No hay localStorage (SSR)');
      return null;
    }
    
    let userStr = localStorage.getItem('user');
    console.log('LoginService - getUser() - userStr:', userStr);
    if(userStr != null){
      let user = JSON.parse(userStr);
      console.log('LoginService - getUser() - parsed user:', user);
      return user;
    }else{
      console.log('LoginService - getUser() - No hay usuario, cerrando sesión');
      this.logout();
      return null;
    }
  }

  public getUserRole(){
    let user = this.getUser();
    console.log('LoginService - getUserRole() - user:', user);
    if(user && user.authorities && user.authorities[0]){
      console.log('LoginService - getUserRole() - role:', user.authorities[0].authority);
      return user.authorities[0].authority;
    }
    console.log('LoginService - getUserRole() - Sin role encontrado');
    return null;
  }

}
