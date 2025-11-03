import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {

  constructor(private http:HttpClient) { }

  public listarCuestionarios(){
    return this.http.get(`${baserUrl}/doctor/`);
  }

  public listarDoctores(){
    return this.http.get(`${baserUrl}/doctor/`);
  }

  public agregarDoctor(doctor:any){
    return this.http.post(`${baserUrl}/doctor/`,doctor);
  }

  public eliminarDoctor(doctorId:any): Observable<any>{
    return this.http.delete(`${baserUrl}/doctor/${doctorId}`, { 
      responseType: 'text',
      observe: 'response'
    });
  }

  public obtenerDoctor(doctorId:any){
    // Temporal: usar endpoint general y filtrar en frontend
    return this.http.get(`${baserUrl}/doctor/`);
  }

  public actualizarDoctor(doctor:any){
    return this.http.put(`${baserUrl}/doctor/`,doctor);
  }

  public listarDoctoresDeUnaServicio(servicioId:any){
    return this.http.get(`${baserUrl}/doctor/servicio/${servicioId}`);
  }

  public obtenerDoctoresActivos(){
    // Temporal: usar endpoint general y filtrar en frontend
    return this.http.get(`${baserUrl}/doctor/`);
  }

  public obtenerDoctoresActivosDeUnaServicio(servicioId:any){
    // Temporal: usar endpoint general y filtrar en frontend
    return this.http.get(`${baserUrl}/doctor/`);
  }
}
