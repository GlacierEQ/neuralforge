import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { IUser } from '../../../../interfaces';
import { LayoutService } from '../../../../services/layout.service';
import { MyAccountComponent } from '../../../my-account/my-account.component';

/**
 * Topbar Component
 * 
 * This component is responsible for rendering the application's top navigation bar.
 * It displays user information and provides a logout function.
 */
@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, RouterLink, MyAccountComponent],
  templateUrl: './topbar.component.html',
})
export class TopbarComponent implements OnInit {
  /** Stores the currently authenticated user */
  public user?: IUser;

  /**
   * Constructor injecting necessary services
   * @param router Handles navigation within the application
   * @param layoutService Manages layout-related functionalities
   * @param authService Handles authentication-related operations
   */
  constructor(
    public router: Router,
    public layoutService: LayoutService,
    public authService: AuthService
  ) {}

  /**
   * Lifecycle hook: Initializes the component by fetching the authenticated user.
   */
  ngOnInit(): void {
    new Promise<void>((resolve) => {
      setTimeout(() => {
        this.user = this.authService.getUser();
        resolve();
      }, 100);
    });
  }

  /**
   * Logs out the user and redirects them to the login page.
   */
  public logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}