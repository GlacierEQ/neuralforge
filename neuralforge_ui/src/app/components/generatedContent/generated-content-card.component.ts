import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-generated-content-card',
  standalone: true, 
  imports: [], 
  templateUrl: './generated-content-card.component.html',
  styleUrls: ['./generated-content-card.component.scss']
})
export class GeneratedContentCardComponent {
  @Input() title: string = '';
  @Input() email: string = '';
  @Input() creationDate: string = '';
  @Input() path: string = '';
  @Input() type: string = '';
}