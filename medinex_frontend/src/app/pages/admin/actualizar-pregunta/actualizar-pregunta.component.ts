import { PreguntaService } from './../../../services/pregunta.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-actualizar-pregunta',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './actualizar-pregunta.component.html',
  styleUrls: ['./actualizar-pregunta.component.css']
})
export class ActualizarPreguntaComponent implements OnInit {

  preguntaId:any = 0;
  pregunta:any;
  doctor:any;

  constructor(
    private route:ActivatedRoute,
    private preguntaService:PreguntaService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.preguntaId = this.route.snapshot.params['preguntaId'];
    this.preguntaService.obtenerPregunta(this.preguntaId).subscribe(
      (data:any) => {
        this.pregunta = data;
        console.log(this.pregunta);
      },
      (error) => {
        console.log(error);
      }
    )
  }

  public actualizarDatosDeLaPregunta(){
    this.preguntaService.actualizarPregunta(this.pregunta).subscribe(
      (data) => {
        Swal.fire('Pregunta actualizada','La pregunta ha sido actualizada con éxito','success').then((e) => {
          this.router.navigate(['/admin/ver-preguntas/'+this.pregunta.doctor.doctorId+'/'+this.pregunta.doctor.titulo]);
        })
      }
    )
  }
}
