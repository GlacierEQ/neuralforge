import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { AppLayoutComponent } from './components/app-layout/app-layout.component';
import { SigUpComponent } from './pages/auth/sign-up/signup.component';
import { UsersComponent } from './pages/users/users.component';
import { AuthGuard } from './guards/auth.guard';
import { AccessDeniedComponent } from './pages/access-denied/access-denied.component';
import { AdminRoleGuard } from './guards/admin-role.guard';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GuestGuard } from './guards/guest.guard';
import { IRoleType } from './interfaces';
import { ProfileComponent } from './pages/profile/profile.component';
import {VerificationComponent} from "./pages/auth/verification/verification.component";

/**
 * Application routes configuration.
 * Defines all available routes and their corresponding components, 
 * including authentication and authorization guards.
 */
export const routes: Routes = [
  /**
   * Route for the login page.
   * Only accessible to unauthenticated users.
   */
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [GuestGuard],
  },

  /**
   * Route for the signup page.
   * Only accessible to unauthenticated users.
   */
  {
    path: 'signup',
    component: SigUpComponent,
    canActivate: [GuestGuard],
  },

  /**
   * Route for the access denied page.
   * Displayed when a user tries to access a restricted area.
   */
  {
    path: 'access-denied',
    component: AccessDeniedComponent,
  },

  /**
   * Redirects the base URL to the login page.
   */
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  /**
   * Redirects to the email verification page.
   */
  {
    path: 'verification',
    component: VerificationComponent,
    canActivate: [GuestGuard],
  },

  /**
   * Main application layout route.
   * Requires authentication and loads nested routes.
   */
  {
    path: 'app',
    component: AppLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      /**
       * Redirects '/app' to '/app/users' by default.
       */
      {
        path: 'app',
        redirectTo: 'users',
        pathMatch: 'full',
      },

      /**
       * Users management page.
       * Only accessible to admin, teacher, and student roles.
       * Displays in the sidebar.
       */
      {
        path: 'users',
        component: UsersComponent,
        canActivate: [AdminRoleGuard],
        data: { 
          authorities: [IRoleType.admin],
          name: 'Users',
          showInSidebar: true
        }
      },

      /**
       * User profile page.
       * Accessible to all roles.
       * Not displayed in the sidebar.
       */
      {
        path: 'profile',
        component: ProfileComponent,
        data: { 
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: 'Profile',
          showInSidebar: false
        }
      },

      /**
       * Dashboard page.
       * Accessible to all roles.
       * Displays in the sidebar.
       */
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: { 
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: 'Dashboard',
          showInSidebar: true
        }
      },
    ],
  },
  /**
   * Failsafe redirect.
   */
  {
    path: '**',
    redirectTo: '/',
    pathMatch: 'full'
  }
];