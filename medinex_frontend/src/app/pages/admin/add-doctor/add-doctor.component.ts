import { Router } from '@angular/router';
import { DoctorService } from '../../../services/doctor.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import  Swal  from 'sweetalert2';
import { ServicioService } from '../../../services/servicio.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-add-doctor',
  standalone: true,
  imports: [MaterialModule, RouterModule],
  templateUrl: './add-doctor.component.html',
  styleUrl: './add-doctor.component.css'
})
export class AddDoctorComponent implements OnInit {

  servicios:any = [];
  isEmergencyMode: boolean = false;
  emergencyServiceId: string = '';

  doctorData = {
    titulo:'',
    descripcion:'',
    puntosMaximos:'',
    numeroDePreguntas:'',
    activo:true,
    emergencia24: false, // Campo para identificar doctores de emergencias
    servicio:{
      servicioId:''
    }
  }

  constructor(
    private servicioService:ServicioService,
    private snack:MatSnackBar,
    private doctorService:DoctorService,
    private router:Router) { }

  ngOnInit(): void {
    this.servicioService.listarServicios().subscribe(
      (dato:any) => {
        this.servicios = dato;
        console.log(this.servicios);
        
        // Buscar o crear servicio de emergencias 24/7
        let emergencyService = this.servicios.find((servicio: any) => 
          servicio.titulo && servicio.titulo.toLowerCase().includes('emergencia')
        );
        
        if (emergencyService) {
          this.emergencyServiceId = emergencyService.servicioId;
        } else {
          // Si no existe, crear el servicio de emergencias
          this.createEmergencyService();
        }
      },(error) => {
        console.log(error);
        Swal.fire('Error !!','Error al cargar los datos','error');
      }
    )
  }

  toggleEmergencyMode() {
    this.isEmergencyMode = !this.isEmergencyMode;
    
    if (this.isEmergencyMode) {
      // Configurar valores por defecto para modo emergencia
      this.doctorData.numeroDePreguntas = '1000';
      this.doctorData.activo = true;
      this.doctorData.emergencia24 = true; // Marcar como doctor de emergencias
      this.doctorData.servicio.servicioId = this.emergencyServiceId;
    } else {
      // Resetear valores para modo normal
      this.doctorData.numeroDePreguntas = '';
      this.doctorData.emergencia24 = false; // No es doctor de emergencias
      this.doctorData.servicio.servicioId = '';
    }
  }

  createEmergencyService() {
    const emergencyServiceData = {
      titulo: 'Emergencias 24/7',
      descripcion: 'Servicio de emergencias médicas disponible las 24 horas del día, 7 días a la semana'
    };

    this.servicioService.agregarServicio(emergencyServiceData).subscribe(
      (response: any) => {
        console.log('Servicio de emergencias creado:', response);
        this.emergencyServiceId = response.servicioId || response.id;
        // Recargar servicios para incluir el nuevo
        this.servicioService.listarServicios().subscribe(
          (datos: any) => {
            this.servicios = datos;
          }
        );
      },
      (error) => {
        console.error('Error al crear servicio de emergencias:', error);
      }
    );
  }

  guardarCuestionario(){
    console.log(this.doctorData);
    
    // Configurar valores automáticos para modo emergencia
    if (this.isEmergencyMode) {
      this.doctorData.numeroDePreguntas = '1000';
      this.doctorData.activo = true;
      this.doctorData.emergencia24 = true; // Importante: marcar como doctor de emergencias
      this.doctorData.servicio.servicioId = this.emergencyServiceId;
    }
    
    if(this.doctorData.titulo.trim() == '' || this.doctorData.titulo == null){
      this.snack.open('El título es requerido','',{
        duration:3000
      });
      return ;
    }

    this.doctorService.agregarDoctor(this.doctorData).subscribe(
      (data) => {
        console.log(data);
        const successMessage = this.isEmergencyMode ? 
          'Doctor de emergencias guardado con éxito. Disponible 24/7.' : 
          'El doctor ha sido guardado con éxito';
        
        Swal.fire('Doctor guardado', successMessage, 'success');
        this.doctorData = {
          titulo : '',
          descripcion : '',
          puntosMaximos : '',
          numeroDePreguntas : '',
          activo:true,
          emergencia24: false,
          servicio:{
            servicioId:''
          }
        }
        
        // Resetear modo emergencia
        this.isEmergencyMode = false;
        
        this.router.navigate(['/admin/doctores']);
      },
      (error) => {
        Swal.fire('Error','Error al guardar el doctor','error');
      }
    )
  }

}
