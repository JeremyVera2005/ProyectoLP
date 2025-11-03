import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MockRespuestaService {

  private readonly STORAGE_KEY = 'medinex_respuestas';

  private mockRespuestas = [
    {
      id: 1,
      usuario: { id: 1, username: 'usuario1', email: 'usuario1@email.com' },
      doctor: { 
        doctorId: 1, 
        nombre: 'Dr. García',
        categoria: { titulo: 'Cardiología' }
      },
      fechaRespuesta: new Date().toISOString(),
      estado: 'completo',
      observaciones: 'Excelente evolución del paciente',
      respuestas: {
        '¿Cómo se siente?': 'Muy bien',
        '¿Tiene dolor?': 'No',
        '¿Toma medicamentos?': 'Sí, los prescritos'
      }
    },
    {
      id: 2,
      usuario: { id: 1, username: 'usuario1', email: 'usuario1@email.com' },
      doctor: { 
        doctorId: 2, 
        nombre: 'Dra. López',
        categoria: { titulo: 'Neurología' }
      },
      fechaRespuesta: new Date(Date.now() - 86400000).toISOString(), // Ayer
      estado: 'pendiente',
      observaciones: null,
      respuestas: {
        '¿Tiene dolores de cabeza?': 'Ocasionalmente',
        '¿Problemas de memoria?': 'No',
        '¿Duerme bien?': 'Sí'
      }
    }
  ];

  constructor() { 
    // Inicializar localStorage con datos mock si no existe
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (!stored) {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.mockRespuestas));
      }
    }
  }

  private getRespuestasFromStorage(): any[] {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return this.mockRespuestas;
    }
    
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch (error) {
        console.error('Error al parsear respuestas del localStorage:', error);
        return this.mockRespuestas;
      }
    }
    return this.mockRespuestas;
  }

  private saveRespuestasToStorage(respuestas: any[]): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(respuestas));
    }
  }

  getMisRespuestas(): Observable<any> {
    // Obtener respuestas del localStorage
    const respuestas = this.getRespuestasFromStorage();
    return of(respuestas).pipe(delay(500));
  }

  getMisRespuestasPorUsuario(usuarioId: number): Observable<any> {
    // Filtrar respuestas por usuario específico
    const respuestas = this.getRespuestasFromStorage();
    const respuestasUsuario = respuestas.filter(r => r.usuario.id === usuarioId);
    return of(respuestasUsuario).pipe(delay(500));
  }

  getAllRespuestas(): Observable<any> {
    // Los administradores ven todas las respuestas del localStorage
    const respuestas = this.getRespuestasFromStorage();
    return of(respuestas).pipe(delay(500));
  }

  crearRespuesta(respuesta: any): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const nuevaRespuesta = {
      id: respuestas.length + 1,
      ...respuesta,
      fechaRespuesta: new Date().toISOString()
    };
    respuestas.push(nuevaRespuesta);
    this.saveRespuestasToStorage(respuestas);
    return of(nuevaRespuesta).pipe(delay(300));
  }

  actualizarRespuesta(id: number, respuesta: any): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const index = respuestas.findIndex(r => r.id === id);
    if (index !== -1) {
      respuestas[index] = { ...respuestas[index], ...respuesta };
      this.saveRespuestasToStorage(respuestas);
      return of(respuestas[index]).pipe(delay(300));
    }
    return throwError('Respuesta no encontrada');
  }

  eliminarRespuesta(id: number): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const index = respuestas.findIndex(r => r.id === id);
    if (index !== -1) {
      respuestas.splice(index, 1);
      this.saveRespuestasToStorage(respuestas);
      return of({ success: true }).pipe(delay(300));
    }
    return throwError('Respuesta no encontrada');
  }

  getEstadisticasRespuestas(): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const hoy = new Date().toDateString();
    const respuestasHoy = respuestas.filter(r => 
      new Date(r.fechaRespuesta).toDateString() === hoy
    ).length;
    
    const completas = respuestas.filter(r => r.estado === 'completo').length;
    const pendientes = respuestas.filter(r => r.estado === 'pendiente').length;
    const canceladas = respuestas.filter(r => r.estado === 'cancelado').length;
    
    return of({
      totalRespuestas: respuestas.length,
      respuestasHoy: respuestasHoy,
      respuestasCompletas: completas,
      respuestasPendientes: pendientes,
      respuestasCanceladas: canceladas
    }).pipe(delay(300));
  }

  getRespuestasByDoctor(doctorId: number): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const respuestasFiltradas = respuestas.filter(r => r.doctor.doctorId === doctorId);
    return of(respuestasFiltradas).pipe(delay(300));
  }

  getRespuestasByFecha(fechaInicio: string, fechaFin: string): Observable<any> {
    const respuestas = this.getRespuestasFromStorage();
    const inicio = new Date(fechaInicio);
    const fin = new Date(fechaFin);
    
    const respuestasFiltradas = respuestas.filter(r => {
      const fechaRespuesta = new Date(r.fechaRespuesta);
      return fechaRespuesta >= inicio && fechaRespuesta <= fin;
    });
    
    return of(respuestasFiltradas).pipe(delay(300));
  }

  // Método de utilidad para limpiar el localStorage (solo para desarrollo/testing)
  limpiarRespuestas(): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.STORAGE_KEY);
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.mockRespuestas));
      console.log('Respuestas del localStorage limpiadas y reinicializadas');
    }
  }

  // Método para debug - ver el contenido actual del localStorage
  debug_verRespuestasEnStorage(): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      const respuestas = this.getRespuestasFromStorage();
      console.log('=== RESPUESTAS EN LOCALSTORAGE ===');
      console.log('Total de respuestas:', respuestas.length);
      console.table(respuestas);
      console.log('===================================');
    }
  }
}
