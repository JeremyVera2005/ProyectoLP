import { MatSnackBar } from '@angular/material/snack-bar';
import { ServicioService } from '../../../services/servicio.service';
import { LoginService } from '../../../services/login.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar-user',
  standalone: true,
  imports: [MaterialModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {

  servicios: any[] = [];
  isLoading: boolean = true;
  usuario: any = null;

  constructor(
    private servicioService: ServicioService,
    private loginService: LoginService,
    private snack: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.usuario = this.loginService.getUser();
    this.cargarServicios();
  }

  cargarServicios(): void {
    this.isLoading = true;
    this.servicioService.listarServicios().subscribe(
      (data: any) => {
        this.servicios = data || [];
        this.isLoading = false;
        console.log('Servicios cargados:', this.servicios);
      },
      (error) => {
        this.isLoading = false;
        this.snack.open('Error al cargar los servicios', '', {
          duration: 3000
        });
        console.error('Error al cargar servicios:', error);
      }
    );
  }

  trackByService(index: number, servicio: any): any {
    return servicio.servicioId;
  }

}
