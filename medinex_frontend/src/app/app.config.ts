import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { provideHttpClient, withInterceptorsFromDi, withFetch } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { LoginService } from './services/login.service';
import { UserService } from './services/user.service';
import { DoctorService } from './services/doctor.service';
import { PreguntaService } from './services/pregunta.service';
import { ServicioService } from './services/servicio.service';
import { RespuestaService } from './services/respuesta.service';
import { MockRespuestaService } from './services/mock-respuesta.service';
import { AdminGuard } from './services/admin.guard';
import { NormalGuard } from './services/normal.guard';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(AppRoutingModule),
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi(), withFetch()),
    provideClientHydration(withEventReplay()),
    LoginService,
    UserService,
    DoctorService,
    PreguntaService,
    ServicioService,
    RespuestaService,
    MockRespuestaService,
    AdminGuard,
    NormalGuard
  ]
};
