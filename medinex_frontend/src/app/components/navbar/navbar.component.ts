import { LoginService } from './../../services/login.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../shared/material.module';
import { MatToolbarModule } from '@angular/material/toolbar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MaterialModule, MatToolbarModule, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {


  isLoggedIn = false;
  user:any = null;

  constructor(public login:LoginService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.login.isLoggedIn();
    this.user = this.login.getUser();
    this.login.loginStatusSubjec.asObservable().subscribe(
      data => {
        this.isLoggedIn = this.login.isLoggedIn();
        this.user = this.login.getUser();
      }
    )
  }

  public logout(){
    this.login.logout();
    window.location.reload();
  }

}
