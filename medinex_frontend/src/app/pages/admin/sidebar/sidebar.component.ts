import { LoginService } from './../../../services/login.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  constructor(public login:LoginService) { }

  ngOnInit(): void {

  }

  public logout(){
    this.login.logout();
    window.location.reload();
  }

}
