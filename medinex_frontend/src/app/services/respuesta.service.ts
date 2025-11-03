import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoginService } from './login.service';
import { MockRespuestaService } from './mock-respuesta.service';
import baseUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class RespuestaService {

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
    private mockService: MockRespuestaService
  ) { }

  // Obtener todas las respuestas para administradores
  public getAllRespuestas(): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas`).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando datos mock para getAllRespuestas');
        return this.mockService.getAllRespuestas();
      })
    );
  }

  // Obtener respuestas por usuario
  public getRespuestasByUsuario(usuarioId: number): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas/usuario/${usuarioId}`).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando datos mock para getRespuestasByUsuario');
        return this.mockService.getMisRespuestas();
      })
    );
  }

  // Obtener respuestas del usuario actual
  public getMisRespuestas(): Observable<any> {
    const usuario = this.loginService.getUser();
    if (usuario && usuario.id) {
      return this.http.get(`${baseUrl}/cita-medica/respuestas/usuario/${usuario.id}`).pipe(
        catchError(error => {
          console.log('Backend no disponible, usando datos mock para getMisRespuestas del usuario:', usuario.id);
          return this.mockService.getMisRespuestasPorUsuario(usuario.id);
        })
      );
    } else {
      // Fallback: usar mock service si no hay usuario
      console.log('No hay usuario logueado, usando datos mock');
      return this.mockService.getMisRespuestas();
    }
  }

  // Obtener respuesta por ID
  public getRespuestaById(respuestaId: number): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas/${respuestaId}`);
  }

  // Crear nueva respuesta
  public crearRespuesta(respuesta: any): Observable<any> {
    return this.http.post(`${baseUrl}/cita-medica/respuestas`, respuesta).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando mock service para crearRespuesta');
        return this.mockService.crearRespuesta(respuesta);
      })
    );
  }

  // Actualizar respuesta (solo para administradores)
  public actualizarRespuesta(respuestaId: number, respuesta: any): Observable<any> {
    return this.http.put(`${baseUrl}/cita-medica/respuestas/${respuestaId}`, respuesta).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando mock service para actualizarRespuesta');
        return this.mockService.actualizarRespuesta(respuestaId, respuesta);
      })
    );
  }

  // Eliminar respuesta (solo para administradores)
  public eliminarRespuesta(respuestaId: number): Observable<any> {
    return this.http.delete(`${baseUrl}/cita-medica/respuestas/${respuestaId}`).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando mock service para eliminarRespuesta');
        return this.mockService.eliminarRespuesta(respuestaId);
      })
    );
  }

  // Obtener estadísticas de respuestas
  public getEstadisticasRespuestas(): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas/estadisticas`).pipe(
      catchError(error => {
        console.log('Backend no disponible, usando mock service para getEstadisticasRespuestas');
        return this.mockService.getEstadisticasRespuestas();
      })
    );
  }

  // Filtrar respuestas por doctor
  public getRespuestasByDoctor(doctorId: number): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas/doctor/${doctorId}`);
  }

  // Obtener respuestas por fecha
  public getRespuestasByFecha(fechaInicio: string, fechaFin: string): Observable<any> {
    return this.http.get(`${baseUrl}/cita-medica/respuestas/fecha?inicio=${fechaInicio}&fin=${fechaFin}`);
  }
}
