import  Swal  from 'sweetalert2';
import { ServicioService } from '../../../services/servicio.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-servicios',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './view-servicios.component.html',
  styleUrl: './view-servicios.component.css'
})
export class ViewServiciosComponent implements OnInit {

  servicios:any = [

  ]

  constructor(
    private servicioService:ServicioService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.cargarServicios();
  }

  cargarServicios(){
    this.servicioService.listarServicios().subscribe(
      (dato:any) => {
        this.servicios = dato;
        console.log('Servicios cargados:', this.servicios);
        if(this.servicios.length > 0) {
          console.log('Estructura del primer servicio:', this.servicios[0]);
          console.log('Claves del primer servicio:', Object.keys(this.servicios[0]));
        }
      },
      (error) => {
        console.log(error);
        Swal.fire('Error !!','Error al cargar los servicios','error');
      }
    )
  }

  eliminarServicio(servicioId:any){
    console.log('ID del servicio a eliminar:', servicioId);
    
    if (!servicioId) {
      Swal.fire('Error', 'ID del servicio no encontrado', 'error');
      return;
    }
    
    Swal.fire({
      title: '¿Eliminar servicio?',
      text: 'Esta acción eliminará el servicio y puede afectar a los doctores asociados. ¿Continuar?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        console.log('Procediendo a eliminar servicio con ID:', servicioId);
        
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
        
        this.servicioService.eliminarServicio(servicioId).subscribe(
          (response) => {
            console.log('Respuesta completa del servidor:', response);
            console.log('Status de la respuesta:', response.status);
            console.log('Body de la respuesta:', response.body);
            
            // Cerrar el loading de SweetAlert antes de mostrar el éxito
            Swal.close();
            
            // Verificar si la eliminación fue exitosa
            if (response.status === 200 || response.status === 204) {
              this.cargarServicios();
              Swal.fire(
                'Eliminado',
                'El servicio ha sido eliminado correctamente',
                'success'
              );
            } else {
              Swal.fire(
                'Advertencia',
                'El servicio fue procesado pero el estado de la respuesta es inesperado',
                'warning'
              );
            }
          },
          (error) => {
            console.error('Error al eliminar:', error);
            console.error('Detalles del error:', {
              status: error.status,
              statusText: error.statusText,
              message: error.message,
              url: error.url,
              errorBody: error.error
            });
            
            // Cerrar el loading de SweetAlert antes de mostrar el error
            Swal.close();
            
            let errorMessage = 'Error desconocido';
            let errorTitle = 'Error';
            
            if (error.status === 500) {
              errorTitle = 'Error del Servidor';
              if (error.error && error.error.message) {
                errorMessage = error.error.message;
              } else {
                errorMessage = 'Error interno del servidor. Posiblemente el servicio está siendo usado por doctores.';
              }
            } else if (error.status === 403) {
              errorTitle = 'Acceso Denegado';
              errorMessage = 'No tienes permisos para eliminar este servicio.';
            } else if (error.status === 404) {
              errorTitle = 'No Encontrado';
              errorMessage = 'El servicio ya no existe.';
            } else if (error.error && error.error.message) {
              errorMessage = error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
            
            Swal.fire(errorTitle, errorMessage, 'error');
          }
        )
      }
    })
  }

  editarServicio(servicioId:any){
    this.router.navigate(['/admin/add-servicio', servicioId]);
  }

}
