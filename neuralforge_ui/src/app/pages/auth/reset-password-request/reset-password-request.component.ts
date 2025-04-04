import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButton } from "@angular/material/button";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatError, MatFormField, MatLabel } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { RouterLink } from "@angular/router";
import { IExceptionResponse } from "../../../interfaces";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: "app-reset-password-request",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatError,
    MatLabel,
    MatFormField,
    MatCard,
    MatCardContent,
    MatInput,
    MatButton,
  ],
  templateUrl: "./reset-password-request.component.html",
  styleUrl: "./reset-password-request.component.scss",
})
export class ResetPasswordRequestComponent implements OnInit {
  resetForm!: FormGroup;

  public validationErrors: string[] = [];
  public successMessage: string = "";
  public isSubmitting = false;
  public requestSent = false;

  constructor(private authService: AuthService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.resetForm = this.fb.group({
      email: [
        "",
        [
          Validators.required,
          Validators.pattern(
            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
          ),
        ],
      ],
    });
  }

  get emailControl() {
    return this.resetForm.get("email");
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.resetForm.get(controlName);
    return control !== null && control.hasError(errorName) && control.touched;
  }

  isSubmitDisabled(): boolean {
    return this.resetForm.invalid || this.isSubmitting;
  }

  public submitForm(event: Event): void {
    event.preventDefault();

    if (this.isSubmitting) {
      return;
    }

    this.validationErrors = [];
    this.successMessage = "";

    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const email = this.emailControl?.value;

    this.authService.requestPasswordReset(email).subscribe({
      next: () => {
        this.successMessage =
          "A password reset email has been sent. Check your inbox.";
        this.resetForm.reset();
        this.isSubmitting = false;
        this.requestSent = true;
      },
      error: (err: IExceptionResponse) => {
        console.error("Error:", err);
        this.validationErrors = Array.isArray(err.error?.exception)
          ? err.error.exception
          : [err.error?.exception || "An unexpected error occurred."];
        this.isSubmitting = false;
      },
      complete: () => {
        this.isSubmitting = false;
      },
    });
  }
}
