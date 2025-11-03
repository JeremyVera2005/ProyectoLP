import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { NavbarComponent } from '../../../components/navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MaterialModule, SidebarComponent, NavbarComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
