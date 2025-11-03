import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core'; // <-- ¡IMPORTADO AQUÍ!
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  imports: [MatCardModule, MatButtonModule, RouterModule],
  // AÑADIDO: El esquema se define en el componente standalone
  schemas: [CUSTOM_ELEMENTS_SCHEMA], 
})
export class HomeComponent {
  
  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }

}