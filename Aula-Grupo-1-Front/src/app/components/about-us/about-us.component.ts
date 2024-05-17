import { Component } from '@angular/core';

@Component({
  selector: 'app-about-us',
  standalone: true,
  imports: [],
  templateUrl: './about-us.component.html',
  styleUrl: './about-us.component.css'
})
export class AboutUsComponent {
  github1: string = '- Javier García';
  github2: string = '- Halder Tarrillo';
  github3: string = '- Marcos Barrera';
  github4: string = '- David Díaz';
  github5: string = '- Adrián Fuentes';
  github6: string = '- Jeremy Benitez';
}
