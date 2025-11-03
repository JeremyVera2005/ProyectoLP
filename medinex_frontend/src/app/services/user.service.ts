import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class UserService {

    constructor(private httpClient: HttpClient) { }

    public añadirUsuario(user:any){
      return this.httpClient.post(`${baserUrl}/usuarios/`,user);
    }

    public obtenerUsuarios(): Observable<any> {
      return this.httpClient.get(`${baserUrl}/usuarios/`);
    }

    public obtenerUsuario(usuarioId: number): Observable<any> {
      return this.httpClient.get(`${baserUrl}/usuarios/${usuarioId}`);
    }

    public actualizarUsuario(usuarioId: number, usuario: any): Observable<any> {
      return this.httpClient.put(`${baserUrl}/usuarios/${usuarioId}`, usuario);
    }

    public eliminarUsuario(usuarioId: number): Observable<any> {
      return this.httpClient.delete(`${baserUrl}/usuarios/${usuarioId}`);
    }
}
