import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { StartComponent } from './pages/user/start/start.component';
import { InstruccionesComponent } from './pages/user/instrucciones/instrucciones.component';
import { LoadDoctorComponent } from './pages/user/load-doctor/load-doctor.component';
import { ActualizarPreguntaComponent } from './pages/admin/actualizar-pregunta/actualizar-pregunta.component';
import { AddPreguntaComponent } from './pages/admin/add-pregunta/add-pregunta.component';
import { ViewDoctorPreguntasComponent } from './pages/admin/view-doctor-preguntas/view-doctor-preguntas.component';
import { AddDoctorComponent } from './pages/admin/add-doctor/add-doctor.component';
import { ViewDoctoresComponent } from './pages/admin/view-doctores/view-doctores.component';
import { AddServicioComponent } from './pages/admin/add-servicio/add-servicio.component';
import { ViewServiciosComponent } from './pages/admin/view-servicios/view-servicios.component';
import { WelcomeComponent } from './pages/admin/welcome/welcome.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NormalGuard } from './services/normal.guard';
import { AdminGuard } from './services/admin.guard';
import { UserDashboardComponent } from './pages/user/user-dashboard/user-dashboard.component';
import { DashboardComponent } from './pages/admin/dashboard/dashboard.component';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { ActualizarDoctorComponent } from './pages/admin/actualizar-doctor/actualizar-doctor.component';
import { EmergenciasComponent } from './pages/admin/emergencias/emergencias.component';
import { ViewRespuestasComponent } from './pages/admin/view-respuestas/view-respuestas.component';
import { MisRespuestasComponent } from './pages/user/mis-respuestas/mis-respuestas.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  {
    path: 'signup',
    component: SignupComponent,
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    pathMatch: 'full'
  },
  {
    path: 'admin',
    component: DashboardComponent,
    canActivate: [AdminGuard],
    children: [
      {
        path: 'profile',
        component: ProfileComponent
      },
      {
        path: '',
        component: WelcomeComponent
      },
      {
        path: 'servicios',
        component: ViewServiciosComponent
      },
      {
        path: 'add-servicio',
        component: AddServicioComponent
      },
      {
        path: 'add-servicio/:servicioId',
        component: AddServicioComponent
      },
      {
        path: 'doctores',
        component: ViewDoctoresComponent
      },
      {
        path: 'add-doctor',
        component: AddDoctorComponent
      },
      {
        path: 'add-doctor/:doctorId',
        component: AddDoctorComponent
      },
      {
        path: 'emergencias',
        component: EmergenciasComponent
      },
      {
        path: 'respuestas',
        component: ViewRespuestasComponent
      },
      {
        path: 'doctor/:doctorId',
        component: ActualizarDoctorComponent
      },
      {
        path: 'ver-preguntas/:doctorId/:titulo',
        component: ViewDoctorPreguntasComponent
      },
      {
        path: 'add-pregunta/:doctorId/:titulo',
        component: AddPreguntaComponent
      },
      {
        path: 'pregunta/:preguntaId',
        component: ActualizarPreguntaComponent
      }
    ]
  },
  {
    path: 'user-dashboard',
    component: UserDashboardComponent,
    canActivate: [NormalGuard],
    children: [
      {
        path: 'mis-respuestas',
        component: MisRespuestasComponent
      },
      {
        path: 'instrucciones/:doctorId',
        component: InstruccionesComponent
      },
      {
        path: ':catId',
        component: LoadDoctorComponent
      }
    ]
  },
  {
    path: "start/:doctorId",
    component: StartComponent,
    canActivate: [NormalGuard]
  },
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
