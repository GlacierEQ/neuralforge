import { Component } from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatAnchor} from "@angular/material/button";


@Component({
  selector: 'app-access-denied',
  standalone: true,
  imports: [
    MatIcon,
    MatAnchor
  ],
  templateUrl: './access-denied.component.html',
  styleUrl: './access-denied.component.scss'
})
export class AccessDeniedComponent {


}