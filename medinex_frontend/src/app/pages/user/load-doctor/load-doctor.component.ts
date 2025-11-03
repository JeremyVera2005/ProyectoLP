import { DoctorService } from '../../../services/doctor.service';
import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-load-doctor',
  standalone: true,
  imports: [MaterialModule, RouterModule, CommonModule],
  templateUrl: './load-doctor.component.html',
  styleUrl: './load-doctor.component.css'
})
export class LoadDoctorComponent implements OnInit {

  catId: any;
  doctores: any[] = [];
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private doctorService: DoctorService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.catId = +params['catId']; // Convertir a número
      console.log('catId recibido:', this.catId);
      console.log('Tipo de catId:', typeof this.catId);
      this.cargarDoctores();
    });
  }

  cargarDoctores(): void {
    this.isLoading = true;
    console.log('Iniciando carga de doctores con catId:', this.catId);
    
    if (this.catId === 0) {
      console.log("Cargando todos los doctores activos");
      console.log("URL que se va a llamar: /api/doctor/activo");
      this.doctorService.obtenerDoctoresActivos().subscribe(
        (data: any) => {
          console.log('Respuesta completa del servidor:', data);
          console.log('Tipo de respuesta:', typeof data);
          console.log('Es array?:', Array.isArray(data));
          
          // Filtrar solo doctores activos
          let todosLosDoctores = data || [];
          this.doctores = todosLosDoctores.filter((doctor: any) => doctor.activo === true || doctor.activo === 1);
          
          this.isLoading = false;
          console.log('Todos los doctores:', todosLosDoctores);
          console.log('Doctores activos filtrados:', this.doctores);
          console.log('Cantidad de doctores activos:', this.doctores.length);
        },
        (error) => {
          console.error('Error completo al cargar doctores:', error);
          console.error('Status del error:', error.status);
          console.error('StatusText del error:', error.statusText);
          console.error('URL del error:', error.url);
          console.error('OK del error:', error.ok);
          console.error('Mensaje del error:', error.message);
          console.error('Headers del error:', error.headers);
          this.isLoading = false;
          this.doctores = [];
        }
      );
    } else {
      console.log("Cargando doctores del servicio:", this.catId);
      console.log("URL que se va a llamar: /api/doctor/servicio/activo/" + this.catId);
      this.doctorService.obtenerDoctoresActivosDeUnaServicio(this.catId).subscribe(
        (data: any) => {
          console.log('Respuesta completa del servidor (por servicio):', data);
          console.log('Filtrando por servicioId:', this.catId);
          
          // Filtrar doctores activos y del servicio específico
          let todosLosDoctores = data || [];
          let doctoresActivos = todosLosDoctores.filter((doctor: any) => doctor.activo === true || doctor.activo === 1);
          
          // Filtrar por servicio específico
          this.doctores = doctoresActivos.filter((doctor: any) => {
            console.log('Doctor:', doctor.titulo, 'ServiceId:', doctor.servicio?.servicioId || doctor.servicioId, 'Comparando con:', this.catId);
            return (doctor.servicio?.servicioId === this.catId) || (doctor.servicioId === this.catId);
          });
          
          this.isLoading = false;
          console.log('Todos los doctores:', todosLosDoctores);
          console.log('Doctores activos:', doctoresActivos);
          console.log('Doctores filtrados por servicio:', this.doctores);
          console.log('Cantidad de doctores del servicio:', this.doctores.length);
        },
        (error) => {
          console.error('Error al cargar doctores del servicio:', error);
          console.error('Status del error:', error.status);
          console.error('StatusText del error:', error.statusText);
          console.error('URL del error:', error.url);
          console.error('OK del error:', error.ok);
          console.error('Mensaje del error:', error.message);
          console.error('Headers del error:', error.headers);
          this.isLoading = false;
          this.doctores = [];
        }
      );
    }
  }
}
