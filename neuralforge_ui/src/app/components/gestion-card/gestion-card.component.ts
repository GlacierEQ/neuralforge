import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-gestion-card',
  standalone: true, 
  imports: [CommonModule], 
  templateUrl: './gestion-card.component.html',
  styleUrls: ['./gestion-card.component.scss']
})
export class GestionCardComponent {
  @Input() title: string = ''; 
  @Input() content: string = ''; 
  @Input() buttons: { text: string, color: string }[] = []; 
}