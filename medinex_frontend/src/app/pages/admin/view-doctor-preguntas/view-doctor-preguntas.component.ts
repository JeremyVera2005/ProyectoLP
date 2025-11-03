import { MatSnackBar } from '@angular/material/snack-bar';
import { PreguntaService } from '../../../services/pregunta.service';
import { LoginService } from '../../../services/login.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-view-doctor-preguntas',
  standalone: true,
  imports: [MaterialModule, RouterModule],
  templateUrl: './view-doctor-preguntas.component.html',
  styleUrl: './view-doctor-preguntas.component.css'
})
export class ViewDoctorPreguntasComponent implements OnInit {

  doctorId:any;
  titulo:any;
  preguntas:any = [];
  loading: boolean = true;
  error: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private preguntaService: PreguntaService,
    private loginService: LoginService,
    private snack: MatSnackBar
  ) { }

  ngOnInit(): void {
    console.log('🚀 ViewDoctorPreguntasComponent - Iniciando...');
    
    // Verificar autenticación
    console.log('=== VERIFICANDO AUTENTICACIÓN ===');
    const isLoggedIn = this.loginService.isLoggedIn();
    const token = this.loginService.getToken();
    const user = this.loginService.getUser();
    
    console.log('¿Está logueado?:', isLoggedIn);
    console.log('Token presente:', !!token);
    console.log('Usuario:', user);
    
    if (!isLoggedIn) {
      console.log('❌ Usuario no autenticado, redirigiendo al login');
      this.snack.open('Sesión expirada. Por favor, inicie sesión nuevamente.', 'Cerrar', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
      this.router.navigate(['/login']);
      return;
    }

    this.doctorId = this.route.snapshot.params['doctorId'];
    this.titulo = this.route.snapshot.params['titulo'];
    
    console.log('=== DEBUG INFO ===');
    console.log('Doctor ID:', this.doctorId);
    console.log('Título:', this.titulo);
    console.log('URL que se va a llamar:', `/api/pregunta/doctor/todos/${this.doctorId}`);
    console.log('URL actual de la página:', window.location.href);
    
    // Validar que el doctorId sea válido
    if (!this.doctorId || this.doctorId === 'undefined' || this.doctorId === 'null') {
      console.error('❌ Doctor ID inválido:', this.doctorId);
      this.error = 'ID de doctor inválido';
      this.loading = false;
      this.snack.open('Error: ID de doctor inválido', 'Cerrar', { duration: 5000 });
      return;
    }
    
    console.log('🔄 Iniciando carga de preguntas...');
    this.loadPreguntas();
  }

  private loadPreguntas(): void {
    this.loading = true;
    this.error = '';
    
    console.log('🔄 Iniciando carga de preguntas...');
    console.log('Doctor ID para la consulta:', this.doctorId);
    
    this.preguntaService.listarPreguntasDelDoctor(this.doctorId).subscribe({
      next: (data: any) => {
        console.log('=== RESPUESTA EXITOSA DEL SERVIDOR ===');
        console.log('Data recibida:', data);
        console.log('Tipo de data:', typeof data);
        console.log('Es array?:', Array.isArray(data));
        
        // Verificar si hay una redirección inesperada
        if (typeof data === 'string' && data.includes('<html>')) {
          console.error('❌ El servidor devolvió HTML en lugar de JSON - posible redirección');
          this.error = 'Error: El servidor devolvió HTML en lugar de datos JSON';
          this.loading = false;
          return;
        }
        
        // Manejar diferentes tipos de respuesta
        let preguntasArray = [];
        
        if (Array.isArray(data)) {
          preguntasArray = data;
          console.log('✅ Data es un array con', data.length, 'elementos');
        } else if (data && typeof data === 'object') {
          // Si la respuesta es un objeto, podría tener las preguntas en una propiedad
          if (data.preguntas && Array.isArray(data.preguntas)) {
            preguntasArray = data.preguntas;
            console.log('✅ Data contiene array de preguntas con', data.preguntas.length, 'elementos');
          } else if (data.data && Array.isArray(data.data)) {
            preguntasArray = data.data;
            console.log('✅ Data.data contiene array con', data.data.length, 'elementos');
          } else {
            console.log('⚠️ Estructura de respuesta inesperada:', data);
            preguntasArray = [];
          }
        } else {
          console.log('⚠️ Tipo de respuesta no reconocido:', typeof data);
          preguntasArray = [];
        }
        
        this.preguntas = preguntasArray;
        this.loading = false;
        
        console.log('✅ Preguntas asignadas al componente:', this.preguntas.length);
        
        if (this.preguntas.length === 0) {
          console.log('⚠️ No se encontraron preguntas para el doctor ID:', this.doctorId);
          this.snack.open('No hay preguntas registradas para este doctor', 'Cerrar', {
            duration: 3000
          });
        } else {
          console.log('🎉 SE CARGARON', this.preguntas.length, 'PREGUNTAS CORRECTAMENTE');
          this.preguntas.forEach((pregunta: any, index: number) => {
            console.log(`Pregunta ${index + 1}:`, pregunta.contenido || 'Sin contenido');
          });
          
          // Forzar detección de cambios
          console.log('🔄 Forzando detección de cambios...');
        }
      },
      error: (error) => {
        console.error('=== ERROR AL CARGAR PREGUNTAS ===');
        console.error('Error completo:', error);
        console.error('Status code:', error.status);
        console.error('Error message:', error.message);
        console.error('URL del error:', error.url);
        
        this.loading = false;
        
        // Manejar diferentes tipos de errores
        switch (error.status) {
          case 401:
            console.log('❌ Error 401: No autorizado - Token inválido o expirado');
            this.loginService.logout();
            this.router.navigate(['/login']);
            this.snack.open('Sesión expirada. Inicie sesión nuevamente.', 'Cerrar', {
              duration: 5000
            });
            break;
            
          case 403:
            console.log('❌ Error 403: Acceso prohibido');
            this.error = 'No tiene permisos para ver las preguntas de este doctor';
            this.snack.open('No tiene permisos para acceder a esta información', 'Cerrar', {
              duration: 5000
            });
            break;
            
          case 404:
            console.log('❌ Error 404: Doctor no encontrado');
            this.error = 'Doctor no encontrado';
            this.snack.open('El doctor especificado no existe', 'Cerrar', {
              duration: 5000
            });
            break;
            
          case 0:
            console.log('❌ Error de conexión: El servidor no responde');
            this.error = 'Error de conexión con el servidor';
            this.snack.open('No se puede conectar con el servidor. Verifique su conexión.', 'Cerrar', {
              duration: 5000
            });
            break;
            
          default:
            console.log('❌ Error desconocido:', error.status);
            this.error = `Error ${error.status}: ${error.message || 'Error desconocido'}`;
            this.snack.open(`Error al cargar preguntas (${error.status})`, 'Cerrar', {
              duration: 5000
            });
        }
      }
    });
  }

  eliminarPregunta(preguntaId:any){
    Swal.fire({
      title:'Eliminar pregunta',
      text:'¿Estás seguro , quieres eliminar?',
      icon:'warning',
      showCancelButton:true,
      confirmButtonColor:'#3085d6',
      cancelButtonColor:'#d33',
      confirmButtonText:'Eliminar',
      cancelButtonText:'Cancelar'
    }).then((resultado) => {
      if(resultado.isConfirmed){
        this.preguntaService.eliminarPregunta(preguntaId).subscribe(
          (data) => {
            this.snack.open('Pregunta eliminada','',{
              duration:3000
            })
            this.preguntas = this.preguntas.filter((pregunta:any) => pregunta.preguntaId != preguntaId);
          },
          (error) => {
            this.snack.open('Error al eliminar la pregunta','',{
              duration:3000
            })
            console.log(error);
          }
        )
      }
    })
  }

  // Método para probar la conexión con un endpoint simple
  private testConnection(): void {
    console.log('🔍 Probando conexión con el servidor...');
    // Podrías probar con un endpoint más simple primero
    // this.loginService.getCurrentUser().subscribe({...});
  }

  // Método para probar el endpoint manualmente
  testEndpoint(): void {
    console.log('🧪 PROBANDO ENDPOINT MANUALMENTE...');
    
    // Probar primero la URL del proxy
    const proxyUrl = `/api/pregunta/doctor/todos/${this.doctorId}`;
    const directUrl = `http://localhost:8080/pregunta/doctor/todos/${this.doctorId}`;
    
    console.log('URL del proxy:', proxyUrl);
    console.log('URL directa:', directUrl);
    
    // Probar con el proxy primero
    console.log('🔍 Probando con proxy...');
    fetch(proxyUrl, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${this.loginService.getToken()}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })
    .then(response => {
      console.log('✅ Proxy Response status:', response.status);
      return response.text();
    })
    .then(text => {
      console.log('✅ Proxy Response text:', text);
      try {
        const data = JSON.parse(text);
        console.log('✅ Proxy Parsed JSON:', data);
        
        // Si funciona, actualizar las preguntas
        if (Array.isArray(data)) {
          this.preguntas = data;
          this.loading = false;
          console.log('🎉 ¡Preguntas cargadas exitosamente vía proxy!');
        }
        return Promise.resolve();
      } catch (e) {
        console.log('❌ Proxy - No es JSON válido:', e);
        console.log('🔍 Probando URL directa...');
        
        // Si el proxy falla, probar directamente
        return fetch(directUrl, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.loginService.getToken()}`,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        })
        .then(directResponse => {
          console.log('✅ Direct Response status:', directResponse.status);
          return directResponse.text();
        })
        .then(directText => {
          console.log('✅ Direct Response text:', directText);
          try {
            const data = JSON.parse(directText);
            console.log('✅ Direct Parsed JSON:', data);
            
            // ¡ACTUALIZAR LAS PREGUNTAS AQUÍ!
            if (Array.isArray(data)) {
              this.preguntas = data;
              this.loading = false;
              this.error = '';
              console.log('🎉 ¡Preguntas cargadas exitosamente vía URL directa!');
              this.snack.open(`¡${data.length} preguntas cargadas exitosamente!`, 'Cerrar', {
                duration: 3000
              });
            }
            
            return data;
          } catch (e) {
            console.log('❌ Direct - No es JSON válido:', e);
            return null;
          }
        });
      }
    })
    .catch(error => {
      console.error('❌ Fetch error:', error);
    });
  }

  // Getters para el template
  get isLoggedIn(): boolean {
    return this.loginService.isLoggedIn();
  }

  get hasToken(): boolean {
    return !!this.loginService.getToken();
  }

  get currentUser(): any {
    return this.loginService.getUser();
  }

  reloadData(): void {
    this.loadPreguntas();
  }

  trackByQuestion(index: number, pregunta: any): any {
    return pregunta.preguntaId;
  }
}
