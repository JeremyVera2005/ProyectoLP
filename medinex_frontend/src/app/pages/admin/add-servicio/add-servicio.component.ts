import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ServicioService } from '../../../services/servicio.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';

@Component({
  selector: 'app-add-servicio',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './add-servicio.component.html',
  styleUrl: './add-servicio.component.css'
})
export class AddServicioComponent implements OnInit {

  servicio = {
    servicioId: 0,
    titulo : '',
    descripcion : ''
  }

  servicioId = 0;
  isEditMode = false;

  constructor(
    private servicioService:ServicioService,
    private snack:MatSnackBar,
    private router:Router,
    private route:ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.servicioId = this.route.snapshot.params['servicioId'];
    if(this.servicioId){
      this.isEditMode = true;
      this.cargarServicio();
    }
  }

  cargarServicio(){
    this.servicioService.obtenerServicio(this.servicioId).subscribe(
      (data:any) => {
        this.servicio = data;
      },
      (error) => {
        console.log(error);
        Swal.fire('Error','Error al cargar el servicio','error');
      }
    )
  }

  formSubmit(){
    if(this.servicio.titulo.trim() == '' || this.servicio.titulo == null){
      this.snack.open("El título es requerido !!",'',{
        duration:3000
      })
      return ;
    }

    if(this.isEditMode){
      // Actualizar servicio existente
      this.servicioService.actualizarServicio(this.servicio).subscribe(
        (dato:any) => {
          Swal.fire('Servicio actualizado','El servicio ha sido actualizado con éxito','success');
          this.router.navigate(['/admin/servicios']);
        },
        (error) => {
          console.log(error);
          Swal.fire('Error !!','Error al actualizar servicio','error')
        }
      )
    } else {
      // Agregar nuevo servicio
      this.servicioService.agregarServicio(this.servicio).subscribe(
        (dato:any) => {
          this.servicio.titulo = '';
          this.servicio.descripcion = '';
          Swal.fire('Servicio agregado','El servicio ha sido agregado con éxito','success');
          this.router.navigate(['/admin/servicios']);
        },
        (error) => {
          console.log(error);
          Swal.fire('Error !!','Error al guardar servicio','error')
        }
      )
    }
  }
}
