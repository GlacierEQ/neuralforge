import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { LayoutService } from '../../../../services/layout.service';
import { AuthService } from '../../../../services/auth.service';
import { NotificationService } from '../../../../services/notification.service';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { routes } from '../../../../app.routes';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { NotificationOverlayComponent} from "../notification-overlay/notification-overlay.component";
import {INotification} from "../../../../interfaces";

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
    MatButtonModule,
    NotificationOverlayComponent
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  animations: [
    trigger('slideInOut', [
      state('open', style({ transform: 'translateX(0)' })),
      state('closed', style({ transform: 'translateX(-100%)' })),
      transition('open <=> closed', animate('300ms ease-in-out'))
    ])
  ]
})
export class SidebarComponent implements OnInit, OnDestroy {
  public layoutService = inject(LayoutService);
  public authService = inject(AuthService);
  public permittedRoutes = this.authService.getPermittedRoutes(
      routes.find(route => route.path === 'app')?.children || []
  );
  public isSidebarOpen = false;
  public userName = 'default';
  public notificationCount: number = 0;
  private pollIntervalId: any;
  allNotifications: INotification[] = [];


  constructor(
      public router: Router,
      private notificationService: NotificationService
  ) {
    const user = localStorage.getItem('auth_user');
    if (user) {
      this.userName = JSON.parse(user)?.name;
    }
  }

  ngOnInit(): void {
    this.fetchNotifications();
    this.pollIntervalId = setInterval(() => this.fetchNotifications(), 30000); // Poll every 30s
  }

  ngOnDestroy(): void {
    if (this.pollIntervalId) clearInterval(this.pollIntervalId);
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar() {
    this.isSidebarOpen = false;
  }

  logout() {
    this.authService.logout();
    this.closeSidebar();
    window.location.reload();
  }
  public showNotifications: boolean = false;
  notifications: INotification[] = [];

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  dismissNotification(id: string) {
    this.notificationService.dismissNotification(id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== id);
        this.updateNotificationCount();
        if (this.notifications.length === 0) {
          this.showNotifications = false;
        }
      },
      error: (err) => {
        console.error('Failed to dismiss notification:', err);
      }
    });
  }



  updateNotificationCount(){
    this.notificationCount = this.notifications.length;
  }


  fetchNotifications() {
    const user = localStorage.getItem('auth_user');
    const email = user ? JSON.parse(user)?.email : null;

    if (email) {
      this.notificationService.getByEmail(email).subscribe({
        next: (response) => {
          this.allNotifications = response;
          this.notifications = response.filter(n => !n.dismissed);
          this.updateNotificationCount();
        },
        error: (err) => {
          console.error('Failed to fetch notifications:', err);
        }
      });
    }

  }

}
