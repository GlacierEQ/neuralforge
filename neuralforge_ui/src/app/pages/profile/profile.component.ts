import { CommonModule } from "@angular/common";
import { Component, OnInit, inject } from "@angular/core";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { SpinnerComponent } from "../../components/spinner/spinner.component";
import { AlertService } from "../../services/alert.service";
import { AuthService } from "../../services/auth.service";
import { ProfileService } from "../../services/profile.service";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { DeleteAccountDialogComponent } from "../../components/dialogs/delete-account-dialog/delete-account-dialog.component";
import { switchMap } from "rxjs/operators";

interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  registrationDate: string;
  lastPasswordChange: string;
}

@Component({
  selector: "app-profile",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
    SpinnerComponent,
  ],
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.scss"],
})
export class ProfileComponent implements OnInit {
  userProfileForm!: FormGroup;
  isEditing = false;
  isDirty = false;
  isLoading = true;
  userProfile: UserProfile = {
    firstName: "",
    lastName: "",
    email: "",
    registrationDate: "",
    lastPasswordChange: "",
  };

  private alertService = inject(AlertService);
  private authService = inject(AuthService);
  private profileService = inject(ProfileService);

  registrationDateControl = new FormControl({ value: "", disabled: true });
  lastPasswordChangeControl = new FormControl({
    value: "",
    disabled: true,
  });

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.disableForm();
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.authService
      .getCurrentUser()
      .pipe(
        catchError((error) => {
          console.error("Error loading user profile:", error);
          this.alertService.displayAlert(
            "error",
            "No se pudo cargar la información del perfil",
            "right",
            "top",
            ["error-snackbar"]
          );
          return of(null);
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((userData) => {
        if (userData) {
          this.userProfile = {
            firstName: userData.name || "",
            lastName: userData.lastName || "",
            email: userData.email || "",
            registrationDate: this.formatDate(userData.createdAt),
            lastPasswordChange:
              this.formatDate(userData.passwordLastChanged) || "Nunca",
          };

          // Update form with fetched data
          this.userProfileForm.patchValue({
            firstName: this.userProfile.firstName,
            lastName: this.userProfile.lastName,
          });

          // Update date controls
          this.registrationDateControl.setValue(
            this.userProfile.registrationDate
          );
          this.lastPasswordChangeControl.setValue(
            this.userProfile.lastPasswordChange
          );
        }
      });
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }

  initForm(): void {
    this.userProfileForm = this.fb.group({
      firstName: [
        { value: this.userProfile.firstName, disabled: true },
        Validators.required,
      ],
      lastName: [
        { value: this.userProfile.lastName, disabled: true },
        Validators.required,
      ],
    });

    this.userProfileForm.valueChanges.subscribe(() => {
      this.isDirty = true;
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (this.isEditing) {
      this.enableForm();
      // Focus firstName input after a short delay to ensure it's enabled
      setTimeout(() => {
        const firstNameInput = document.querySelector(
          'input[formControlName="firstName"]'
        ) as HTMLInputElement;
        firstNameInput?.focus();
      }, 0);
    } else {
      this.disableForm();
    }
  }

  enableForm(): void {
    this.userProfileForm.get("firstName")?.enable();
    this.userProfileForm.get("lastName")?.enable();
  }

  disableForm(): void {
    this.userProfileForm.get("firstName")?.disable();
    this.userProfileForm.get("lastName")?.disable();
    this.isDirty = false;
  }

  cancelEdit(): void {
    this.userProfileForm.patchValue({
      firstName: this.userProfile.firstName,
      lastName: this.userProfile.lastName,
    });
    this.toggleEdit();
  }

  onSubmit(): void {
    if (this.userProfileForm.valid) {
      const formValues = this.userProfileForm.getRawValue();

      const userData = {
        name: formValues.firstName,
        lastName: formValues.lastName,
      };

      this.isLoading = true;
      this.profileService
        .updateUserProfile(userData)
        .pipe(
          catchError((error) => {
            console.error("Error updating profile:", error);
            this.alertService.displayAlert(
              "error",
              "No se pudo actualizar el perfil",
              "right",
              "top",
              ["error-snackbar"]
            );
            return of(null);
          }),
          finalize(() => {
            this.isLoading = false;
          })
        )
        .subscribe((response) => {
          if (response) {
            // Update the user profile with the response data
            this.userProfile = {
              ...this.userProfile,
              firstName: response.name || "",
              lastName: response.lastName || "",
            };

            this.toggleEdit();
            this.alertService.displayAlert(
              "success",
              "Perfil modificado correctamente",
              "right",
              "top",
              ["success-snackbar"]
            );
          }
        });
    }
  }

  changeEmail(): void {
    console.log("Change email clicked");
    alert("Not implemented yet");
  }

  changePassword(): void {
    console.log("Change password clicked");
    alert("Not implemented yet");
  }

  deleteAccount(): void {
    const dialogRef = this.dialog.open(DeleteAccountDialogComponent, {
      width: "400px",
    });

    dialogRef
      .afterClosed()
      .pipe(
        switchMap((result) => {
          if (result) {
            this.isLoading = true;
            return this.profileService.deleteAccount();
          }
          return of(null);
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe(
        (result) => {
          this.alertService.displayAlert(
            "success",
            "Cuenta eliminada correctamente",
            "center",
            "top",
            ["success-snackbar"]
          );

          // Logout and redirect to login page
          this.authService.logout();
          this.router.navigate(["/login"]);
        },
        (error) => {
          this.alertService.displayAlert(
            "error",
            "Error al eliminar la cuenta",
            "center",
            "top",
            ["error-snackbar"]
          );
          console.error("Error deleting account:", error);
        }
      );
  }
}
