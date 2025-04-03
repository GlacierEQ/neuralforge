import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * AppComponent
 * ------------
 * This is the root component of the Angular application.
 * It serves as the main entry point and container for all routed views.
 * 
 * - Uses the `RouterOutlet` directive to render components based on the active route.
 * - Configured as a standalone component to allow direct use of standalone imports.
 */
@Component({
  selector: 'app-root',                         // Selector used in index.html
  standalone: true,                             // Enables standalone component usage
  templateUrl: './app.component.html',          // External HTML layout
  styleUrls: ['./app.component.scss'],          // External SCSS for global styles
})
export class AppComponent {
  // No logic defined yet. This component is primarily used for routing display.
}

