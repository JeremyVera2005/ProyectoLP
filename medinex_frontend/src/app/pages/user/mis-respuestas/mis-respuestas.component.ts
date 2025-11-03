import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatSortModule } from '@angular/material/sort';
import { RespuestaService } from '../../../services/respuesta.service';
import { DoctorService } from '../../../services/doctor.service';
import { LoginService } from '../../../services/login.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mis-respuestas',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatSortModule
  ],
  templateUrl: './mis-respuestas.component.html',
  styleUrls: ['./mis-respuestas.component.css']
})
export class MisRespuestasComponent implements OnInit {

  misRespuestas: any[] = [];
  respuestasFiltradas: any[] = [];
  doctores: any[] = [];
  filtroDoctor: string = '';
  filtroFecha: string = '';
  loading: boolean = false;
  usuario: any = null;

  displayedColumns: string[] = ['doctor', 'fecha', 'estado', 'observaciones', 'acciones'];

  constructor(
    private respuestaService: RespuestaService,
    private doctorService: DoctorService,
    private loginService: LoginService
  ) { }

  ngOnInit(): void {
    this.usuario = this.loginService.getUser();
    
    // Verificar autenticación
    if (!this.usuario || !this.loginService.isLoggedIn()) {
      console.warn('Usuario no autenticado, redirigiendo...');
      Swal.fire({
        title: 'Sesión requerida',
        text: 'Debes iniciar sesión para ver tus respuestas',
        icon: 'warning',
        confirmButtonText: 'Ir al login'
      }).then(() => {
        window.location.href = '/login';
      });
      return;
    }
    
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    
    // Cargar mis respuestas con manejo de errores mejorado
    this.respuestaService.getMisRespuestas().subscribe({
      next: (data: any) => {
        console.log('Respuestas cargadas:', data);
        this.misRespuestas = Array.isArray(data) ? data : [];
        this.respuestasFiltradas = [...this.misRespuestas];
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error al cargar mis respuestas:', error);
        this.loading = false;
        
        // Si el error es 404, mostrar mensaje amigable
        if (error.status === 404) {
          console.log('No se encontraron respuestas - esto es normal si no has completado cuestionarios');
          this.misRespuestas = [];
          this.respuestasFiltradas = [];
        } else {
          Swal.fire({
            title: 'Error',
            text: 'No se pudieron cargar tus respuestas. Por favor, intenta más tarde.',
            icon: 'error',
            confirmButtonText: 'Entendido'
          });
        }
      }
    });

    // Cargar doctores para el filtro
    this.doctorService.listarDoctores().subscribe({
      next: (data: any) => {
        this.doctores = Array.isArray(data) ? data : [];
      },
      error: (error: any) => {
        console.error('Error al cargar doctores:', error);
        this.doctores = [];
      }
    });
  }

  aplicarFiltros(): void {
    this.respuestasFiltradas = this.misRespuestas.filter(respuesta => {
      let cumpleFiltros = true;

      if (this.filtroDoctor && this.filtroDoctor !== '') {
        cumpleFiltros = cumpleFiltros && respuesta.doctor.doctorId.toString() === this.filtroDoctor;
      }

      if (this.filtroFecha && this.filtroFecha !== '') {
        const fechaRespuesta = new Date(respuesta.fechaRespuesta).toDateString();
        const fechaFiltro = new Date(this.filtroFecha).toDateString();
        cumpleFiltros = cumpleFiltros && fechaRespuesta === fechaFiltro;
      }

      return cumpleFiltros;
    });
  }

  limpiarFiltros(): void {
    this.filtroDoctor = '';
    this.filtroFecha = '';
    this.respuestasFiltradas = [...this.misRespuestas];
  }

  verDetalleRespuesta(respuesta: any): void {
    let respuestasHtml = '';
    if (respuesta.respuestas && typeof respuesta.respuestas === 'object') {
      for (const [pregunta, resp] of Object.entries(respuesta.respuestas)) {
        respuestasHtml += `<strong>${pregunta}:</strong> ${resp}<br>`;
      }
    } else {
      respuestasHtml = respuesta.respuestas || 'Sin respuestas registradas';
    }

    Swal.fire({
      title: 'Mis Respuestas al Cuestionario',
      html: `
        <div style="text-align: left;">
          <p><strong>Doctor:</strong> ${respuesta.doctor.nombre}</p>
          <p><strong>Fecha:</strong> ${new Date(respuesta.fechaRespuesta).toLocaleString()}</p>
          <p><strong>Estado:</strong> ${this.getEstadoLabel(respuesta.estado)}</p>
          <p><strong>Observaciones del Doctor:</strong> ${respuesta.observaciones || 'Sin observaciones'}</p>
          <hr>
          <p><strong>Mis respuestas:</strong></p>
          <div>${respuestasHtml}</div>
        </div>
      `,
      width: '600px',
      confirmButtonText: 'Cerrar'
    });
  }

  getEstadoPuntuacion(puntuacion: number): string {
    if (!puntuacion) return 'pendiente';
    if (puntuacion >= 80) return 'excelente';
    if (puntuacion >= 60) return 'bueno';
    if (puntuacion >= 40) return 'regular';
    return 'malo';
  }

  getColorPuntuacion(puntuacion: number): string {
    if (!puntuacion) return 'warn';
    if (puntuacion >= 80) return 'primary';
    if (puntuacion >= 60) return 'accent';
    return 'warn';
  }

  exportarMisRespuestas(): void {
    const csvData = this.respuestasFiltradas.map(respuesta => ({
      Doctor: respuesta.doctor.nombre,
      Fecha: new Date(respuesta.fechaRespuesta).toLocaleString(),
      Puntuacion: respuesta.puntuacion || 'Pendiente',
      Observaciones: respuesta.observaciones || 'Sin observaciones'
    }));

    const csvContent = this.convertToCSV(csvData);
    this.downloadCSV(csvContent, 'mis_respuestas_medicas.csv');
  }

  private convertToCSV(data: any[]): string {
    if (data.length === 0) return '';
    
    const headers = Object.keys(data[0]).join(',');
    const rows = data.map(row => Object.values(row).join(','));
    
    return [headers, ...rows].join('\n');
  }

  private downloadCSV(content: string, filename: string): void {
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }

  getPuntuacionTexto(puntuacion: number): string {
    if (!puntuacion) return 'Sin calificar';
    return `${puntuacion}/100`;
  }

  getIconoPuntuacion(puntuacion: number): string {
    if (!puntuacion) return 'schedule';
    if (puntuacion >= 80) return 'star';
    if (puntuacion >= 60) return 'thumb_up';
    if (puntuacion >= 40) return 'thumbs_up_down';
    return 'thumb_down';
  }

  getEstadoLabel(estado: string): string {
    switch (estado) {
      case 'pendiente': return 'Pendiente';
      case 'completo': return 'Completo';
      case 'cancelado': return 'Cancelado';
      default: return 'Sin estado';
    }
  }

  getEstadoColor(estado: string): string {
    switch (estado) {
      case 'completo': return 'primary';
      case 'pendiente': return 'warn';
      case 'cancelado': return 'accent';
      default: return 'basic';
    }
  }

  getEstadoIcon(estado: string): string {
    switch (estado) {
      case 'completo': return 'check_circle';
      case 'pendiente': return 'schedule';
      case 'cancelado': return 'cancel';
      default: return 'help';
    }
  }
}
