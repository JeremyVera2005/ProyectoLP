import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core'; // <-- CUSTOM_ELEMENTS_SCHEMA AÑADIDO AQUÍ
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';

// Services
import { LoginService } from './services/login.service';
import { UserService } from './services/user.service';
import { DoctorService } from './services/doctor.service';
import { PreguntaService } from './services/pregunta.service';
import { ServicioService } from './services/servicio.service';
import { AdminGuard } from './services/admin.guard';
import { NormalGuard } from './services/normal.guard';
import { authInterceptorProviders } from './services/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    RouterModule,
    AppRoutingModule
  ],
  providers: [
    provideClientHydration(withEventReplay()),
    LoginService,
    UserService,
    DoctorService,
    PreguntaService,
    ServicioService,
    AdminGuard,
    NormalGuard,
    authInterceptorProviders
  ],
  bootstrap: [AppComponent],
  // Esta línea le indica a Angular que acepte etiquetas HTML no estándar (como <df-messenger>)
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }