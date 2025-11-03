import { LoginService } from './../../services/login.service';
import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../shared/material.module';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  user:any = null;

  constructor(private loginService:LoginService) { }

  ngOnInit(): void {
    this.user = this.loginService.getUser();
    /*this.loginService.getCurrentUser().subscribe(
      (user:any) => {
        this.user = user;
      },
      (error) => {
        alert("error");
      }
    )*/
  }

}
