import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import { LayoutService } from '../../../../services/layout.service';
import { AuthService } from '../../../../services/auth.service';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { routes } from '../../../../app.routes';

// 🚀 Import Angular Animations
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],  // ✅ Fix incorrect property name
  animations: [
    trigger('slideInOut', [
      state('open', style({ transform: 'translateX(0)' })),
      state('closed', style({ transform: 'translateX(-100%)' })),
      transition('open <=> closed', animate('300ms ease-in-out'))
    ])
  ]
})
export class SidebarComponent {
  public layoutService = inject(LayoutService);
  public authService = inject(AuthService);
  public permittedRoutes = this.authService.getPermittedRoutes(routes.find(route => route.path === 'app')?.children || []);
  public isSidebarOpen = false;
  public userName = "default";

  constructor(public router: Router) {
    let user = localStorage.getItem('auth_user');
    if (user) {
      this.userName = JSON.parse(user)?.name;
    }
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar() {
    this.isSidebarOpen = false;
  }

  navigateToProfile() {
    this.router.navigate(['/profile']).then(() => {
      this.closeSidebar();
    });
  }

  logout() {
    this.authService.logout();
    this.closeSidebar();
    window.location.reload();
  }
}

