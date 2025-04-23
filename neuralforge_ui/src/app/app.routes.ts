import { Routes } from "@angular/router";
import { AppLayoutComponent } from "./components/app-layout/app-layout.component";
import { AdminRoleGuard } from "./guards/admin-role.guard";
import { AuthGuard } from "./guards/auth.guard";
import { editProfileDeactivateGuard } from "./guards/edit-profile-deactivate.guard";
import { GuestGuard } from "./guards/guest.guard";
import { IRoleType } from "./interfaces";
import { AccessDeniedComponent } from "./pages/access-denied/access-denied.component";
import { LoginComponent } from "./pages/auth/login/login.component";
import { ResetPasswordRequestComponent } from "./pages/auth/reset-password-request/reset-password-request.component";
import { PasswordResetComponent } from "./pages/auth/reset-password/reset-password.component";
import { SigUpComponent } from "./pages/auth/sign-up/signup.component";
import { VerificationComponent } from "./pages/auth/verification/verification.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { ProfileComponent } from "./pages/profile/profile.component";
import { LearningProjectComponent } from "./pages/projects/learning/learning-project.component";
import { ProgrammedGoalProjectComponent } from "./pages/projects/programmed-goal/programmed-goal-project.component";
import { TeachingProjectComponent } from "./pages/projects/teaching/teaching-project.component";
import { UsersComponent } from "./pages/users/users.component";
import {ProjectGlobalViewComponent} from "./pages/projects/project-global-view/project-global-view.component";
import {VirtualClassDashboardComponent} from "./pages/virtual-class-dashboard/virtual-class-dashboard.component";
import {VirtualClass} from "./pages/virtual-class/virtual-class.component";
import { QuizDetailComponent } from "./components/quiz/quiz-detail/quiz-detail.component";
import { QuizTakeComponent } from "./components/quiz/quiz-take/quiz-take.component";
import { QuizResultComponent } from "./components/quiz/quiz-result/quiz-result.component";

/**
 * Application routes configuration.
 * Defines all available routes and their corresponding components,
 * including authentication and authorization guards.
 */
export const routes: Routes = [
  {
    path: "login",
    component: LoginComponent,
    canActivate: [GuestGuard],
  },

  {
    path: "signup",
    component: SigUpComponent,
    canActivate: [GuestGuard],
  },

  {
    path: "access-denied",
    component: AccessDeniedComponent,
  },

  {
    path: "",
    redirectTo: "login",
    pathMatch: "full",
  },

  {
    path: "verification",
    component: VerificationComponent,
    canActivate: [GuestGuard],
  },

  {
    path: "reset-password-request",
    component: ResetPasswordRequestComponent,
    canActivate: [GuestGuard],
  },

  {
    path: "reset-password",
    component: PasswordResetComponent,
    canActivate: [GuestGuard],
  },

  {
    path: "app",
    component: AppLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: "app",
        redirectTo: "users",
        pathMatch: "full",
      },

      {
        path: "project/programmed_goal/:projectId",
        component: ProgrammedGoalProjectComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Programmed Goal",
          showInSidebar: false,
        },
      },
      {
        path: "project/learning/:projectId",
        component: LearningProjectComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Learning Project",
          showInSidebar: false,
        },
      },
      {
        path: "projects/:id/quiz/:quizId",
        component: QuizDetailComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Quiz Details",
          showInSidebar: false,
        },
      },
      {
        path: "quizzes/:quizId/take",
        component: QuizTakeComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Take Quiz",
          showInSidebar: false,
        },
      },
      {
        path: "quizzes/:quizId/attempts/:attemptId",
        component: QuizResultComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Quiz Result",
          showInSidebar: false,
        },
      },
      {
        path: "project/teaching/:projectId",
        component: TeachingProjectComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Teaching Project",
          showInSidebar: false,
        },
      },
      {
        path: "users",
        component: UsersComponent,
        canActivate: [AdminRoleGuard],
        data: {
          authorities: [IRoleType.admin],
          name: "Users",
          showInSidebar: true,
        },
      },
      {
        path: "projects",
        component: ProjectGlobalViewComponent,
        canActivate: [AdminRoleGuard],
        data: {
          authorities: [IRoleType.admin],
          name: "Projects",
          showInSidebar: true,
        },
      },
      {
        path: "virtual-class-dashboard",
        component: VirtualClassDashboardComponent,

        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Virtual Classes",
          showInSidebar: true,
        },
      },
      {
        path: "virtual-class/:classId",
        component: VirtualClass,

        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Virtual Classes",
          showInSidebar: false,
        },
      },
      {
        path: "profile",
        component: ProfileComponent,
        canDeactivate: [editProfileDeactivateGuard],
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Profile",
          showInSidebar: false,
        },
      },

      {
        path: "dashboard",
        component: DashboardComponent,
        data: {
          authorities: [IRoleType.admin, IRoleType.teacher, IRoleType.student],
          name: "Dashboard",
          showInSidebar: true,
        },
      },
    ],
  },

  {
    path: "**",
    redirectTo: "/",
    pathMatch: "full",
  },
];
