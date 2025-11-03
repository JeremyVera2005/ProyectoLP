import { PreguntaService } from './../../../services/pregunta.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-pregunta',
  standalone: true,
  imports: [MaterialModule, RouterModule],
  templateUrl: './add-pregunta.component.html',
  styleUrl: './add-pregunta.component.css'
})
export class AddPreguntaComponent implements OnInit {

  doctorId:any;
  titulo:any;
  pregunta:any = {
    doctor : {},
    contenido : '',
    opcion1 : '',
    opcion2 : '',
    opcion3 : '',
    opcion4 : '',
    respuesta : ''
  }

  constructor(
    private route:ActivatedRoute,
    private router:Router,
    private preguntaService:PreguntaService) { }

  ngOnInit(): void {
    this.doctorId = this.route.snapshot.params['doctorId'];
    this.titulo = this.route.snapshot.params['titulo'];
    this.pregunta.doctor['doctorId'] = this.doctorId;
  }

  formSubmit(){
    if(this.pregunta.contenido.trim() == '' || this.pregunta.contenido == null){
      return;
    }
    if(this.pregunta.opcion1.trim() == '' || this.pregunta.opcion1 == null){
      return;
    }
    if(this.pregunta.opcion2.trim() == '' || this.pregunta.opcion2 == null){
      return;
    }
    if(this.pregunta.opcion3.trim() == '' || this.pregunta.opcion3 == null){
      return;
    }
    if(this.pregunta.opcion4.trim() == '' || this.pregunta.opcion4 == null){
      return;
    }
    if(this.pregunta.respuesta.trim() == '' || this.pregunta.respuesta == null){
      return;
    }

    this.preguntaService.guardarPregunta(this.pregunta).subscribe(
      (data) => {
        Swal.fire({
          title: 'Pregunta guardada',
          text: 'La pregunta ha sido agregada con éxito',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then((result) => {
          // Redirigir a la página de preguntas del doctor específico
          this.router.navigate([`/admin/ver-preguntas/${this.doctorId}/${this.titulo}`]);
        });
      },(error) => {
        Swal.fire('Error','Error al guardar la pregunta en la base de datos','error');
        console.log(error);
      }
    )
  }

}
