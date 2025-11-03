import  Swal  from 'sweetalert2';
import { ServicioService } from '../../../services/servicio.service';
import { DoctorService } from '../../../services/doctor.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MaterialModule } from '../../../shared/material.module';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-actualizar-doctor',
  standalone: true,
  imports: [MaterialModule, FormsModule, CommonModule],
  templateUrl: './actualizar-doctor.component.html',
  styleUrls: ['./actualizar-doctor.component.css']
})
export class ActualizarDoctorComponent implements OnInit {

  constructor(
    private route:ActivatedRoute,
    private doctorService:DoctorService,
    private servicioService:ServicioService,
    private router:Router) { }

  doctorId = 0;
  doctor:any;
  servicios:any;
  isEmergencyMode: boolean = false;
  emergencyServiceId: string = '';

  ngOnInit(): void {
    this.doctorId = this.route.snapshot.params['doctorId'];
    console.log('Doctor ID obtenido de la ruta:', this.doctorId);
    
    this.doctorService.obtenerDoctor(this.doctorId).subscribe(
      (data: any) => {
        console.log('Respuesta completa del servidor:', data);
        // Filtrar el doctor específico por ID
        let todosLosDoctores = data || [];
        this.doctor = todosLosDoctores.find((doc: any) => doc.doctorId == this.doctorId);
        
        if (this.doctor) {
          console.log('Doctor cargado:', this.doctor);
          
          // Detectar si es un doctor de emergencias y activar el modo correspondiente
          this.detectEmergencyMode();
        } else {
          console.error('Doctor no encontrado con ID:', this.doctorId);
          Swal.fire('Error', 'Doctor no encontrado', 'error');
          this.router.navigate(['/admin/doctores']);
        }
      },
      (error) => {
        console.error('Error al cargar doctor:', error);
        Swal.fire('Error', 'No se pudo cargar la información del doctor', 'error');
      }
    )

    this.servicioService.listarServicios().subscribe(
      (data:any) => {
        this.servicios = data;
        console.log('Servicios cargados:', this.servicios);
        
        // Buscar servicio de emergencias para tener su ID disponible
        let emergencyService = this.servicios.find((servicio: any) => 
          servicio.titulo && servicio.titulo.toLowerCase().includes('emergencia')
        );
        
        if (emergencyService) {
          this.emergencyServiceId = emergencyService.servicioId;
        }
      },
      (error) => {
        console.error('Error al cargar servicios:', error);
        Swal.fire('Error', 'Error al cargar los servicios', 'error');
      }
    )
  }

  detectEmergencyMode() {
    // Detectar si es doctor de emergencias basado en varios criterios
    if (this.doctor) {
      const isEmergency = this.doctor.emergencia24 === true || 
                         this.doctor.numeroDePreguntas == 1000 ||
                         (this.doctor.servicio && this.doctor.servicio.titulo && 
                          this.doctor.servicio.titulo.toLowerCase().includes('emergencia')) ||
                         (this.doctor.titulo && this.doctor.titulo.toLowerCase().includes('emergencia'));
      
      if (isEmergency) {
        this.isEmergencyMode = true;
        console.log('Modo emergencia activado para el doctor:', this.doctor.titulo);
      }
    }
  }

  public actualizarDatos(event?: Event){
    if (event) {
      event.preventDefault();
    }
    
    console.log('Datos del doctor a actualizar:', this.doctor);
    
    // Si está en modo emergencia, asegurar que los valores estén configurados correctamente
    if (this.isEmergencyMode) {
      this.doctor.numeroDePreguntas = '1000';
      this.doctor.activo = true;
      this.doctor.emergencia24 = true;
      this.doctor.servicio.servicioId = this.emergencyServiceId;
    }
    
    this.doctorService.actualizarDoctor(this.doctor).subscribe(
      (data) => {
        console.log('Respuesta de actualización:', data);
        const successMessage = this.isEmergencyMode ? 
          'Doctor de emergencias actualizado con éxito. Disponible 24/7.' : 
          'El doctor ha sido actualizado con éxito';
        
        Swal.fire('Doctor actualizado', successMessage, 'success').then(
          (e) => {
            // Navegar de vuelta a la vista correspondiente
            if (this.isEmergencyMode) {
              this.router.navigate(['/admin/emergencias']);
            } else {
              this.router.navigate(['/admin/doctores']);
            }
          }
        );
      },
      (error) => {
        console.error('Error al actualizar doctor:', error);
        Swal.fire('Error en el sistema','No se ha podido actualizar el doctor','error');
      }
    )
  }
}
