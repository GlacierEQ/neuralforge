import { CommonModule } from "@angular/common";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { FormsModule, NgModel } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../../services/auth.service";

declare const google: any; // Avoid TypeScript errors for Google API

/**
 * LoginComponent - Handles user authentication through email/password and Google Sign-In.
 */
@Component({
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.scss",
})
export class LoginComponent implements OnInit, AfterViewInit {
  /** Flag to toggle password visibility */
  public showPassword: boolean = false;

  /** Stores any login error message */
  public loginError!: string;

  /** Reference to the email input field */
  @ViewChild("email") emailModel!: NgModel;

  /** Reference to the password input field */
  @ViewChild("password") passwordModel!: NgModel;

  /** Model for login form */
  public loginForm: { email: string; password: string } = {
    email: "",
    password: "",
  };

  /**
   * Constructor - Injects required services.
   * @param router Angular Router for navigation
   * @param authService Service for authentication API calls
   */
  constructor(private router: Router, private authService: AuthService) {}
  /**
   * Toggles visibility for the password input.
   */
  public togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Initializes Google login callback handler.
   */
  ngOnInit(): void {
    (window as any).handleCredentialResponse = (response: any) => {
      this.handleGoogleLogin(response.credential);
    };
  }

  /**
   * Loads Google Sign-In functionality after view initialization.
   */
  ngAfterViewInit(): void {
    this.loadGoogleSignIn();
  }

  /**
   * Loads Google Sign-In API and initializes login button.
   */
  private loadGoogleSignIn(): void {
    setTimeout(() => {
      if (google && google.accounts) {
        google.accounts.id.initialize({
          client_id: "YOUR_GOOGLE_CLIENT_ID",
          callback: (response: any) =>
            this.handleGoogleLogin(response.credential),
        });

        google.accounts.id.prompt();
      } else {
        console.error("Google API not loaded yet.");
      }
    }, 500);
  }

  /**
   * Handles user login via email and password.
   * @param event Form submit event
   */
  public handleLogin(event: Event): void {
    event.preventDefault();

    if (!this.emailModel.valid) this.emailModel.control.markAsTouched();
    if (!this.passwordModel.valid) this.passwordModel.control.markAsTouched();

    if (this.emailModel.valid && this.passwordModel.valid) {
      this.authService.login(this.loginForm).subscribe({
        next: () => this.router.navigateByUrl("/app/dashboard"),
        error: (err: any) => {
          this.loginError = err.error?.exception || "An error occurred";
        },
      });
    }
  }

  /**
   * Handles authentication using Google login.
   * @param token Google authentication token
   */
  private handleGoogleLogin(token: string): void {
    this.authService.sendGoogleTokenToApi(token).subscribe({
      next: () => {
        window.location.reload();
      },
      error: (err: any) => {
        if (err.status === 404) {
          this.loginError =
            "This account is not registered. Please create an account first.";
        } else if (
          err?.error?.exception?.includes("Account verification pending")
        ) {
          this.loginError =
            "Your account is not verified. Please check your email for verification.";
        } else {
          this.loginError =
            err?.error?.message ||
            err?.message ||
            "An error occurred during authentication.";
        }
      },
    });
  }
}
