import { ActivatedRoute, Router } from '@angular/router';
import { DoctorService } from '../../../services/doctor.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-instrucciones',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './instrucciones.component.html',
  styleUrls: ['./instrucciones.component.css']
})
export class InstruccionesComponent implements OnInit {

  doctorId:any;
  doctor:any = new Object();

  constructor(
    private doctorService:DoctorService,
    private route:ActivatedRoute,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.doctorId = this.route.snapshot.params['doctorId'];
    this.doctorService.obtenerDoctor(this.doctorId).subscribe(
      (data:any) => {
        console.log(data);
        this.doctor = data;
      },
      (error) => {
        console.log(error);
      }
    )
  }

  empezarDoctor(){
    Swal.fire({
      title:'¿Quieres comenzar?',
      showCancelButton:true,
      cancelButtonText:'Cancelar',
      confirmButtonText:'Empezar',
      icon:'info'
    }).then((result:any) => {
      if(result.isConfirmed){
        this.router.navigate(['/start/'+this.doctorId]);
      }
    })
  }

}
