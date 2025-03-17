import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import {IExceptionResponse, IUser} from '../../../interfaces';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SigUpComponent {
  public signUpError!: [String?];
  @ViewChild('name') nameModel!: NgModel;
  @ViewChild('lastname') lastnameModel!: NgModel;
  @ViewChild('email') emailModel!: NgModel;
  @ViewChild('password') passwordModel!: NgModel;

  public user: IUser = {};
  public showPassword: boolean = false;
  constructor(private router: Router,
    private authService: AuthService
  ) {}

  public get isSignUpDisabled(): boolean {
    return !this.nameModel?.valid || !this.emailModel?.valid || !this.passwordModel?.valid;
  }

  public togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  public handleSignup(event: Event) {
    event.preventDefault();
    this.signUpError = [];
    if (!this.nameModel.valid) {
      this.nameModel.control.markAsTouched();
    }
    if (!this.emailModel.valid) {
      this.emailModel.control.markAsTouched();
    }
    if (!this.passwordModel.valid) {
      this.passwordModel.control.markAsTouched();
    }
    if (this.emailModel.valid && this.passwordModel.valid) {
      this.authService.signup(this.user).subscribe({
        next: () => {
          this.router.navigate(['/verification'], { queryParams: { email: this.user.email } });
        },
        error: (err: IExceptionResponse) => {
          this.signUpError = Array.isArray(err.error.exception)
              ? err.error.exception
              : [err.error.exception];
        },
      });
    }
  }

}
