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
import { MatSelectModule } from "@angular/material/select";
import { AlertService } from "../../services/alert.service";

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
  ],
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.scss"],
})
export class ProfileComponent implements OnInit {
  userProfileForm!: FormGroup;
  isEditing = false;
  isDirty = false;
  userProfile: UserProfile = {
    firstName: "Enrique",
    lastName: "Alpízar",
    email: "ealpizarp@ucenfotec.ac.cr",
    registrationDate: "10/01/2025",
    lastPasswordChange: "16/03/2025",
  };
  private alertService = inject(AlertService);

  registrationDateControl = new FormControl({ value: "", disabled: true });
  lastPasswordChangeControl = new FormControl({
    value: "",
    disabled: true,
  });

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForm();
    this.disableForm();

    // Set the date values
    this.registrationDateControl.setValue(this.userProfile.registrationDate);
    this.lastPasswordChangeControl.setValue(
      this.userProfile.lastPasswordChange
    );
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

      // Update the user profile
      this.userProfile = {
        ...this.userProfile,
        firstName: formValues.firstName,
        lastName: formValues.lastName,
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
  }

  changeEmail(): void {
    console.log("Change email clicked");
    alert("Not implemented yet");
  }

  changePassword(): void {
    console.log("Change password clicked");
    alert("Not implemented yet");
  }
}
