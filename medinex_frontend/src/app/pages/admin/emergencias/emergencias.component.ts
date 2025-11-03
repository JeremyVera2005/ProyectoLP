import Swal from 'sweetalert2';
import { DoctorService } from '../../../services/doctor.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-emergencias',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './emergencias.component.html',
  styleUrl: './emergencias.component.css'
})
export class EmergenciasComponent implements OnInit {

  doctores: any = [];
  doctoresEmergencia: any = [];

  constructor(
    private doctorService: DoctorService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cargarDoctoresEmergencia();
  }

  cargarDoctoresEmergencia() {
    this.doctorService.listarDoctores().subscribe(
      (data: any) => {
        this.doctores = data;
        console.log('Todos los doctores:', this.doctores);
        
        // Filtrar doctores de emergencias usando múltiples criterios
        this.doctoresEmergencia = this.doctores.filter((doctor: any) => {
          // Criterio 1: Campo emergencia24 marcado como true
          if (doctor.emergencia24 === true) {
            return true;
          }
          
          // Criterio 2: Número de consultas = 1000 (doctores 24/7)
          if (doctor.numeroDePreguntas === '1000' || doctor.numeroDePreguntas === 1000) {
            return true;
          }
          
          // Criterio 3: Servicio contiene "emergencia"
          if (doctor.servicio && doctor.servicio.titulo && 
              doctor.servicio.titulo.toLowerCase().includes('emergencia')) {
            return true;
          }
          
          // Criterio 4: Título del doctor contiene "emergencia"
          if (doctor.titulo && doctor.titulo.toLowerCase().includes('emergencia')) {
            return true;
          }
          
          return false;
        });
        
        console.log('Doctores de emergencia filtrados:', this.doctoresEmergencia);
        
        if (this.doctoresEmergencia.length > 0) {
          console.log('Estructura del primer doctor de emergencia:', this.doctoresEmergencia[0]);
          console.log('Claves del primer doctor:', Object.keys(this.doctoresEmergencia[0]));
        }
      },
      (error: any) => {
        console.log(error);
        Swal.fire('Error', 'Error al cargar los doctores de emergencia', 'error');
      }
    );
  }

  eliminarDoctor(doctorId: any) {
    console.log('ID del doctor a eliminar:', doctorId);
    
    if (!doctorId) {
      Swal.fire('Error', 'ID del doctor no encontrado', 'error');
      return;
    }
    
    Swal.fire({
      title: '¿Eliminar doctor de emergencias?',
      text: 'Esta acción eliminará al doctor del servicio de emergencias 24/7. ¿Continuar?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        console.log('Procediendo a eliminar doctor con ID:', doctorId);
        
        // Mostrar loading
        Swal.fire({
          title: 'Eliminando...',
          text: 'Por favor espere',
          icon: 'info',
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });
        
        this.doctorService.eliminarDoctor(doctorId).subscribe(
          (response) => {
            console.log('Respuesta completa del servidor:', response);
            
            // Cerrar el loading de SweetAlert antes de mostrar el éxito
            Swal.close();
            
            // Verificar si la eliminación fue exitosa
            if (response.status === 200 || response.status === 204) {
              this.cargarDoctoresEmergencia();
              Swal.fire(
                'Eliminado',
                'El doctor ha sido eliminado del servicio de emergencias',
                'success'
              );
            } else {
              Swal.fire(
                'Advertencia',
                'El doctor fue procesado pero el estado de la respuesta es inesperado',
                'warning'
              );
            }
          },
          (error) => {
            console.error('Error al eliminar:', error);
            
            // Cerrar el loading de SweetAlert antes de mostrar el error
            Swal.close();
            
            let errorMessage = 'Error desconocido';
            let errorTitle = 'Error';
            
            if (error.status === 500) {
              errorTitle = 'Error del Servidor';
              errorMessage = 'Error interno del servidor. Posiblemente el doctor está asignado a servicios activos.';
            } else if (error.status === 403) {
              errorTitle = 'Acceso Denegado';
              errorMessage = 'No tienes permisos para eliminar este doctor.';
            } else if (error.status === 404) {
              errorTitle = 'No Encontrado';
              errorMessage = 'El doctor ya no existe.';
            } else if (error.error && error.error.message) {
              errorMessage = error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
            
            Swal.fire(errorTitle, errorMessage, 'error');
          }
        );
      }
    });
  }

  editarDoctor(doctorId: any) {
    this.router.navigate(['/admin/doctor', doctorId]);
  }

  getDoctorStatus(doctor: any): string {
    const now = new Date();
    const currentHour = now.getHours();
    
    // Simulación de disponibilidad basada en la hora actual
    if (currentHour >= 22 || currentHour < 6) {
      return 'Turno Nocturno';
    } else if (currentHour >= 6 && currentHour < 14) {
      return 'Turno Mañana';
    } else {
      return 'Turno Tarde';
    }
  }

  getDoctorAvailability(doctor: any): boolean {
    // Los doctores de emergencia siempre están disponibles
    return true;
  }
}
