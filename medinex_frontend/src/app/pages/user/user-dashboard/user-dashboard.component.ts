import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';
import { SidebarComponent as UserSidebarComponent } from '../sidebar/sidebar.component';
import { NavbarComponent } from '../../../components/navbar/navbar.component';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [MaterialModule, UserSidebarComponent, NavbarComponent],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
