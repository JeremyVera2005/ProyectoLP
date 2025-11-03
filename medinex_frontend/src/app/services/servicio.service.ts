import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class ServicioService {

  constructor(private http:HttpClient) { }

  public listarServicios(){
    return this.http.get(`${baserUrl}/servicio/`);
  }

  public agregarServicio(servicio:any){
    return this.http.post(`${baserUrl}/servicio/`,servicio);
  }

  public eliminarServicio(servicioId:any): Observable<any>{
    return this.http.delete(`${baserUrl}/servicio/${servicioId}`, { 
      responseType: 'text',
      observe: 'response'
    });
  }

  public actualizarServicio(servicio:any){
    return this.http.put(`${baserUrl}/servicio/`,servicio);
  }

  public obtenerServicio(servicioId:any){
    return this.http.get(`${baserUrl}/servicio/${servicioId}`);
  }
}
