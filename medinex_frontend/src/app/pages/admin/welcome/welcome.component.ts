import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../shared/material.module';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [MaterialModule],
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {

  }


}
