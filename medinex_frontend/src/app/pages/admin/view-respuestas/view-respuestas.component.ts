import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSortModule } from '@angular/material/sort';
import { RespuestaService } from '../../../services/respuesta.service';
import { DoctorService } from '../../../services/doctor.service';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';

/**
 * Componente para visualizar respuestas de usuarios
 * ACCESO RESTRINGIDO: Solo disponible para administradores
 * Los usuarios normales solo pueden ver sus propias respuestas a través del componente "MisRespuestasComponent"
 */
@Component({
  selector: 'app-view-respuestas',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatChipsModule,
    MatTooltipModule,
    MatSortModule
  ],
  templateUrl: './view-respuestas.component.html',
  styleUrls: ['./view-respuestas.component.css']
})
export class ViewRespuestasComponent implements OnInit {

  respuestas: any[] = [];
  respuestasFiltradas: any[] = [];
  doctores: any[] = [];
  usuarios: any[] = [];
  filtroDoctor: string = '';
  filtroUsuario: string = '';
  filtroFecha: string = '';
  filtroEstado: string = '';
  loading: boolean = false;
  estadisticas: any = {};

  // Estados disponibles
  estadosDisponibles = [
    { value: 'pendiente', label: 'Pendiente' },
    { value: 'completo', label: 'Completo' },
    { value: 'cancelado', label: 'Cancelado' }
  ];

  displayedColumns: string[] = ['id', 'usuario', 'doctor', 'fecha', 'estado', 'respuesta', 'acciones'];

  constructor(
    private respuestaService: RespuestaService,
    private doctorService: DoctorService,
    private userService: UserService
  ) { 
    // Exponer método de debug en desarrollo
    if (typeof window !== 'undefined') {
      (window as any).debugRespuestas = () => {
        console.log('=== DEBUG RESPUESTAS ADMIN ===');
        console.log('Respuestas cargadas:', this.respuestas);
        console.log('Respuestas filtradas:', this.respuestasFiltradas);
      };
    }
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    
    // Cargar respuestas
    this.respuestaService.getAllRespuestas().subscribe(
      (data: any) => {
        console.log('Admin - Respuestas cargadas desde localStorage:', data);
        this.respuestas = data;
        this.respuestasFiltradas = [...this.respuestas];
        this.loading = false;
      },
      (error) => {
        console.error('Error al cargar respuestas:', error);
        this.loading = false;
        Swal.fire('Error', 'Error al cargar las respuestas', 'error');
      }
    );

    // Cargar doctores
    this.doctorService.listarDoctores().subscribe(
      (data: any) => {
        this.doctores = data;
      },
      (error) => {
        console.error('Error al cargar doctores:', error);
      }
    );

    // Cargar usuarios
    this.userService.obtenerUsuarios().subscribe(
      (data: any) => {
        this.usuarios = data;
      },
      (error: any) => {
        console.error('Error al cargar usuarios:', error);
      }
    );

    // Cargar estadísticas
    this.respuestaService.getEstadisticasRespuestas().subscribe(
      (data: any) => {
        this.estadisticas = data;
      },
      (error) => {
        console.error('Error al cargar estadísticas:', error);
      }
    );
  }

  aplicarFiltros(): void {
    this.respuestasFiltradas = this.respuestas.filter(respuesta => {
      let cumpleFiltros = true;

      if (this.filtroDoctor && this.filtroDoctor !== '') {
        cumpleFiltros = cumpleFiltros && respuesta.doctor.doctorId.toString() === this.filtroDoctor;
      }

      if (this.filtroUsuario && this.filtroUsuario !== '') {
        cumpleFiltros = cumpleFiltros && respuesta.usuario.id.toString() === this.filtroUsuario;
      }

      if (this.filtroFecha && this.filtroFecha !== '') {
        const fechaRespuesta = new Date(respuesta.fechaRespuesta).toDateString();
        const fechaFiltro = new Date(this.filtroFecha).toDateString();
        cumpleFiltros = cumpleFiltros && fechaRespuesta === fechaFiltro;
      }

      if (this.filtroEstado && this.filtroEstado !== '') {
        cumpleFiltros = cumpleFiltros && respuesta.estado === this.filtroEstado;
      }

      return cumpleFiltros;
    });
  }

  limpiarFiltros(): void {
    this.filtroDoctor = '';
    this.filtroUsuario = '';
    this.filtroFecha = '';
    this.filtroEstado = '';
    this.respuestasFiltradas = [...this.respuestas];
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
      title: 'Detalle de Respuesta',
      html: `
        <div style="text-align: left;">
          <p><strong>Usuario:</strong> ${respuesta.usuario.username}</p>
          <p><strong>Doctor:</strong> ${respuesta.doctor.nombre}</p>
          <p><strong>Fecha:</strong> ${new Date(respuesta.fechaRespuesta).toLocaleString()}</p>
          <p><strong>Estado:</strong> ${this.getEstadoLabel(respuesta.estado)}</p>
          <p><strong>Observaciones:</strong> ${respuesta.observaciones || 'Ninguna'}</p>
          <hr>
          <p><strong>Respuestas del cuestionario:</strong></p>
          <div>${respuestasHtml}</div>
        </div>
      `,
      width: '600px',
      confirmButtonText: 'Cerrar'
    });
  }

  editarRespuesta(respuesta: any): void {
    Swal.fire({
      title: 'Editar Respuesta',
      html: `
        <div style="text-align: left;">
          <label for="estado">Estado:</label>
          <select id="estado" class="swal2-input">
            <option value="pendiente" ${respuesta.estado === 'pendiente' ? 'selected' : ''}>Pendiente</option>
            <option value="completo" ${respuesta.estado === 'completo' ? 'selected' : ''}>Completo</option>
            <option value="cancelado" ${respuesta.estado === 'cancelado' ? 'selected' : ''}>Cancelado</option>
          </select>
          
          <label for="observaciones">Observaciones:</label>
          <textarea id="observaciones" class="swal2-textarea" placeholder="Observaciones del administrador">${respuesta.observaciones || ''}</textarea>
        </div>
      `,
      showCancelButton: true,
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const estado = (document.getElementById('estado') as HTMLSelectElement).value;
        const observaciones = (document.getElementById('observaciones') as HTMLTextAreaElement).value;
        
        return {
          estado: estado,
          observaciones: observaciones
        };
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const datosActualizados = {
          ...respuesta,
          estado: result.value.estado,
          observaciones: result.value.observaciones
        };

        this.respuestaService.actualizarRespuesta(respuesta.id, datosActualizados).subscribe(
          (response) => {
            Swal.fire('Éxito', 'Respuesta actualizada correctamente', 'success');
            this.cargarDatos();
          },
          (error) => {
            console.error('Error al actualizar respuesta:', error);
            Swal.fire('Error', 'Error al actualizar la respuesta', 'error');
          }
        );
      }
    });
  }

  eliminarRespuesta(respuestaId: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        this.respuestaService.eliminarRespuesta(respuestaId).subscribe(
          (response) => {
            Swal.fire('Eliminado', 'La respuesta ha sido eliminada', 'success');
            this.cargarDatos();
          },
          (error) => {
            console.error('Error al eliminar respuesta:', error);
            Swal.fire('Error', 'Error al eliminar la respuesta', 'error');
          }
        );
      }
    });
  }

  exportarDatos(): void {
    // Función para exportar datos a CSV
    const csvData = this.respuestasFiltradas.map(respuesta => ({
      ID: respuesta.id,
      Usuario: respuesta.usuario.username,
      Doctor: respuesta.doctor.nombre,
      Fecha: new Date(respuesta.fechaRespuesta).toLocaleString(),
      Puntuacion: respuesta.puntuacion || 'No asignada',
      Observaciones: respuesta.observaciones || 'Ninguna'
    }));

    const csvContent = this.convertToCSV(csvData);
    this.downloadCSV(csvContent, 'respuestas_usuarios.csv');
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

  getNombreDoctor(doctorId: number): string {
    const doctor = this.doctores.find(d => d.doctorId === doctorId);
    return doctor ? doctor.nombre : 'Doctor no encontrado';
  }

  getNombreUsuario(usuarioId: number): string {
    const usuario = this.usuarios.find(u => u.id === usuarioId);
    return usuario ? usuario.username : 'Usuario no encontrado';
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
