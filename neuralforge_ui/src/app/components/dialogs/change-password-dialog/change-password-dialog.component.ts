import { Component, inject } from '@angular/core';
import {
    FormBuilder,
    FormGroup,
    ReactiveFormsModule,
} from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../../services/alert.service';
import { ProfileService } from '../../../services/profile.service';
import {AuthService} from "../../../services/auth.service";

@Component({
    selector: 'app-change-password-dialog',
    standalone: true,
    templateUrl: './change-password-dialog.component.html',
    styleUrls: ['./change-password-dialog.component.scss'],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
    ],
})
export class ChangePasswordDialogComponent {
    fb = inject(FormBuilder);
    alertService = inject(AlertService);
    profileService = inject(ProfileService);
    dialogRef = inject(MatDialogRef<ChangePasswordDialogComponent>);
    authService: AuthService = inject(AuthService);

    passwordForm: FormGroup = this.fb.group({
        currentPassword: [''],
        newPassword: [''],
    });

    isSubmitting = false;
    showCurrentPassword = false;
    showNewPassword = false;
    errorMessages: string[] = [];

    toggleCurrentVisibility() {
        this.showCurrentPassword = !this.showCurrentPassword;
    }

    toggleNewVisibility() {
        this.showNewPassword = !this.showNewPassword;
    }

    onSubmit() {
        this.isSubmitting = true;
        this.errorMessages = [];

        this.profileService.updatePassword(this.passwordForm.value).subscribe({
            next: () => {
                this.alertService.displayAlert(
                    'info',
                    'Your password was changed successfully. Please log in again.',
                    'center',
                    'top',
                    ['info-snackbar']
                );

                setTimeout(() => {
                    this.authService.logout();
                    window.location.reload();
                }, 2000);
            },
            error: (err) => {
                const exceptionData = err?.error;

                if (exceptionData?.exception) {
                    if (Array.isArray(exceptionData.exception)) {
                        this.errorMessages = exceptionData.exception;
                    } else if (typeof exceptionData.exception === 'string') {
                        this.errorMessages = [exceptionData.exception];
                    }
                } else {
                    this.errorMessages = ['An unexpected error occurred.'];
                }

                this.alertService.displayAlert(
                    'error',
                    'Failed to update password',
                    'right',
                    'top',
                    ['error-snackbar']
                );

                this.isSubmitting = false;
            },
            complete: () => {
                this.isSubmitting = false;
            },
        });
    }


    cancel() {
        this.dialogRef.close();
    }
}
