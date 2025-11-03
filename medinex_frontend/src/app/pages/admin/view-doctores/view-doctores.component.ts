import  Swal  from 'sweetalert2';
import { DoctorService } from '../../../services/doctor.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-view-doctores',
  standalone: true,
  imports: [MaterialModule, RouterModule],
  templateUrl: './view-doctores.component.html',
  styleUrl: './view-doctores.component.css'
})
export class ViewDoctoresComponent implements OnInit {

  doctores: any = [];
  doctoresRegulares: any = []; // Doctores filtrados sin emergencias

  constructor(private doctorService:DoctorService) { }

  ngOnInit(): void {
    this.cargarDoctoresRegulares();
  }

  cargarDoctoresRegulares() {
    this.doctorService.listarCuestionarios().subscribe(
      (dato:any) => {
        this.doctores = dato;
        console.log('Todos los doctores cargados:', this.doctores);
        
        // Filtrar doctores regulares (excluir doctores de emergencias)
        this.doctoresRegulares = this.doctores.filter((doctor: any) => {
          // Excluir si tiene el campo emergencia24 como true
          if (doctor.emergencia24 === true) {
            return false;
          }
          
          // Excluir si tiene 1000 consultas (indicador de doctor 24/7)
          if (doctor.numeroDePreguntas === '1000' || doctor.numeroDePreguntas === 1000) {
            return false;
          }
          
          // Excluir si su servicio es "Emergencias 24/7"
          if (doctor.servicio && doctor.servicio.titulo && 
              doctor.servicio.titulo.toLowerCase().includes('emergencia')) {
            return false;
          }
          
          // Excluir si su título contiene "emergencia"
          if (doctor.titulo && doctor.titulo.toLowerCase().includes('emergencia')) {
            return false;
          }
          
          // Incluir todos los demás doctores
          return true;
        });
        
        console.log('Doctores regulares filtrados:', this.doctoresRegulares);
        console.log(`Total: ${this.doctores.length}, Regulares: ${this.doctoresRegulares.length}, Emergencias: ${this.doctores.length - this.doctoresRegulares.length}`);
      },
      (error) => {
        console.log(error);
        Swal.fire('Error','Error al cargar los doctores','error');
      }
    )
  }

  eliminarDoctor(doctorId:any){
    Swal.fire({
      title:'Eliminar doctor',
      text:'¿Estás seguro de eliminar el doctor?',
      icon:'warning',
      showCancelButton:true,
      confirmButtonColor:'#3085d6',
      cancelButtonColor:'#d33',
      confirmButtonText:'Eliminar',
      cancelButtonText:'Cancelar'
    }).then((result) => {
      if(result.isConfirmed){
        this.doctorService.eliminarDoctor(doctorId).subscribe(
          (data) => {
            // Actualizar ambas listas después de eliminar
            this.doctoresRegulares = this.doctoresRegulares.filter((doctor:any) => doctor.doctorId != doctorId);
            this.doctores = this.doctores.filter((doctor:any) => doctor.doctorId != doctorId);
            Swal.fire('Doctor eliminado','El doctor ha sido eliminado de la base de datos','success');
          },
          (error) => {
            Swal.fire('Error','Error al eliminar el Doctor','error');
          }
        )
      }
    })
  }

  trackByDoctorId(index: number, doctor: any): any {
    return doctor.doctorId;
  }
}
