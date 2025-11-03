import { PreguntaService } from './../../../services/pregunta.service';
import { RespuestaService } from './../../../services/respuesta.service';
import { ActivatedRoute, Router } from '@angular/router';
import { LocationStrategy } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../../services/login.service';
import { MaterialModule } from '../../../shared/material.module';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-start',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './start.component.html',
  styleUrls: ['./start.component.css']
})
export class StartComponent implements OnInit {

  user: any;
  doctorId: any;
  preguntas: any;
  respuestas: any[] = [];
  doctorInfo: any; // Para almacenar información del doctor
  nombreCompleto: string = ''; // Para almacenar el nombre completo del usuario

  esEnviado = false;
  timer: any;
  fechaYHoraActual: string = ''; // Definir la propiedad

  constructor(
    private locationSt: LocationStrategy,
    private route: ActivatedRoute,
    private preguntaService: PreguntaService,
    private respuestaService: RespuestaService,
    private loginService: LoginService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.prevenirElBotonDeRetroceso();
    this.user = this.loginService.getUser();
    this.nombreCompleto = `${this.user?.nombre || 'Usuario'} ${this.user?.apellido || 'Sin apellido'}`.trim();
    this.doctorId = this.route.snapshot.params['doctorId'];
    this.cargarPreguntas();
  }

  cargarPreguntas() {
    this.preguntaService.listarPreguntasDelDoctorParaLaPrueba(this.doctorId).subscribe(
      (data: any) => {
        this.preguntas = data;
        
        // Almacenar información del doctor de manera segura
        if (this.preguntas && this.preguntas.length > 0 && this.preguntas[0].doctor) {
          this.doctorInfo = this.preguntas[0].doctor;
        } else {
          this.doctorInfo = { titulo: 'Doctor no especificado' };
        }
        
        this.timer = this.preguntas.length * 2 * 60;

        this.preguntas.forEach((p: any) => {
          p['respuestaDada'] = '';
        });
        this.iniciarTemporizador();
      },
      (error) => {
        Swal.fire('Error', 'Error al cargar las preguntas de la prueba', 'error');
      }
    );
  }

  iniciarTemporizador() {
    let t = window.setInterval(() => {
      if (this.timer <= 0) {
        this.evaluarDoctor();
        clearInterval(t);
      } else {
        this.timer--;
      }
    }, 1000);
  }

  prevenirElBotonDeRetroceso() {
    history.pushState(null, null!, location.href);
    this.locationSt.onPopState(() => {
      history.pushState(null, null!, location.href);
    });
  }

  enviarCuestionario() {
    Swal.fire({
      title: '¿Quieres enviar?',
      showCancelButton: true,
      cancelButtonText: 'Cancelar',
      confirmButtonText: 'Enviar',
      icon: 'info',
      showCloseButton: true,
      allowEscapeKey: true,
      allowOutsideClick: true
    }).then((e) => {
      if (e.isConfirmed) {
        this.evaluarDoctor();
      }
    });
  }

  evaluarDoctor() {
    this.respuestas = this.preguntas.map((pregunta: any) => ({
      preguntaId: pregunta.preguntaId,
      respuestaDada: pregunta.respuestaDada
    }));

    // Obtener la fecha y hora actual con mejor formato
    const ahora = new Date();
    this.fechaYHoraActual = ahora.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });

    // Evaluar doctor con el sistema original
    this.preguntaService.evaluarDoctor(this.preguntas).subscribe(
      (data: any) => {
        this.esEnviado = true;
        console.log('Cuestionario completado - Mostrando pantalla de resultados');
        
        // Guardar respuestas en el nuevo microservicio
        this.guardarRespuestaEnMicroservicio();
        
        console.log('Datos del doctor:', this.doctorInfo);
        console.log('Fecha y hora:', this.fechaYHoraActual);
        console.log('Nombre completo:', this.nombreCompleto);
      },
      (error) => {
        console.log('Error al evaluar doctor:', error);
        // Establecer esEnviado = true aún con error para mostrar la pantalla de resultados
        this.esEnviado = true;
        // Aún así, intentar guardar las respuestas
        this.guardarRespuestaEnMicroservicio();
      }
    );
  }

  guardarRespuestaEnMicroservicio() {
    // Verificar que tengamos usuario y doctor
    if (!this.user || !this.doctorId) {
      console.error('Faltan datos de usuario o doctor');
      return;
    }

    // Crear objeto de respuesta para el microservicio
    const respuestasObj: { [key: string]: string } = {};
    
    this.preguntas.forEach((pregunta: any) => {
      respuestasObj[pregunta.contenido] = pregunta.respuestaDada || 'Sin respuesta';
    });

    const respuestaCuestionario = {
      usuario: {
        id: this.user.id,
        username: this.user.username
      },
      doctor: {
        doctorId: parseInt(this.doctorId),
        nombre: this.doctorInfo?.titulo || 'Doctor'
      },
      respuestas: respuestasObj,
      fechaRespuesta: new Date().toISOString(),
      estado: 'pendiente', // Estado inicial
      observaciones: null
    };

    console.log('Enviando respuesta al microservicio:', respuestaCuestionario);

    this.respuestaService.crearRespuesta(respuestaCuestionario).subscribe({
      next: (response: any) => {
        console.log('Respuesta guardada exitosamente:', response);
        // Mantener esEnviado = true para mostrar la pantalla de resultados
        // No redirigir automáticamente
      },
      error: (error: any) => {
        console.error('Error al guardar respuesta:', error);
        // Aún así mantener la pantalla de resultados
        // El usuario puede decidir qué hacer desde ahí
      }
    });
  }

  obtenerHoraFormateada() {
    let mm = Math.floor(this.timer / 60);
    let ss = this.timer - mm * 60;
    return `${mm} : min : ${ss} seg`;
  }

  imprimirPagina() {
    window.print();
  }

  volverAlInicio() {
    this.router.navigate(['/user-dashboard/0']).then(() => {
      console.log('Navegación al dashboard de usuario completada');
    }).catch(error => {
      console.error('Error al navegar al dashboard:', error);
    });
  }

  obtenerNombreDoctor(): string {
    return this.doctorInfo?.titulo || 'Doctor no especificado';
  }
}