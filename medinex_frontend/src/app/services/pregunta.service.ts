import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { throwError } from 'rxjs';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class PreguntaService {

  constructor(private http:HttpClient) { }

  public listarPreguntasDelDoctor(doctorId:any): Observable<any>{
    const url = `${baserUrl}/pregunta/doctor/todos/${doctorId}`;
    console.log('🔗 PreguntaService - URL completa del endpoint:', url);
    
    return this.http.get(url).pipe(
      catchError((error) => {
        console.error('❌ PreguntaService - Error:', error);
        console.error('❌ PreguntaService - URL que falló:', url);
        return throwError(() => error);
      })
    );
  }

  public guardarPregunta(pregunta:any){
    return this.http.post(`${baserUrl}/pregunta/`,pregunta);
  }

  public eliminarPregunta(preguntaId:any){
    return this.http.delete(`${baserUrl}/pregunta/${preguntaId}`);
  }

  public actualizarPregunta(pregunta:any){
    return this.http.put(`${baserUrl}/pregunta/`,pregunta);
  }

  public obtenerPregunta(preguntaId:any){
    return this.http.get(`${baserUrl}/pregunta/${preguntaId}`);
  }

  public listarPreguntasDelDoctorParaLaPrueba(doctorId:any){
    return this.http.get(`${baserUrl}/pregunta/doctor/todos/${doctorId}`);
  }

  public evaluarDoctor(preguntas:any){
    return this.http.post(`${baserUrl}/pregunta/evaluar-doctor`,preguntas);
  }
}
