import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

// Importing necessary Angular Material Modules
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {IExceptionResponse} from "../../../interfaces";

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule // Added MatIconModule for visibility toggler
  ],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class PasswordResetComponent implements OnInit {
  token: string | null = null;
  newPassword: string = '';
  confirmPassword: string = '';
  validationErrors: string[] = [];
  successMessage: string = '';

  // Password visibility toggler
  showNewPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || null;
      if (this.token == null) this.router.navigate(['/login']);
    });
  }

  togglePasswordVisibility(field: 'new' | 'confirm') {
    if (field === 'new') {
      this.showNewPassword = !this.showNewPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  submitForm() {
    this.validationErrors = [];

    if (!this.token) {
      this.validationErrors.push("Invalid request. No token provided.");
      return;
    }


    if (this.newPassword !== this.confirmPassword) {
      this.validationErrors.push("Passwords do not match.");
      return;
    }

    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: (response) => {
        console.log(response)
        this.successMessage = "Password successfully reset. Redirecting to login...";
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err: IExceptionResponse) => {

        console.log(err)

        this.validationErrors = Array.isArray(err.error.exception)
            ? err.error.exception
            : [err.error.exception];
      }
    });
  }

}
