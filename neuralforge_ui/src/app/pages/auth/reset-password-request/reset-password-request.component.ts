import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm, NgModel } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { IExceptionResponse } from '../../../interfaces';
import { Observable } from 'rxjs';
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";

@Component({
    selector: 'app-reset-password-request',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, MatError, MatLabel, MatFormField, MatCard, MatCardContent, MatInput, MatButton],
    templateUrl: './reset-password-request.component.html',
    styleUrl: './reset-password-request.component.scss'
})
export class ResetPasswordRequestComponent implements OnInit {
    /** Referencia al campo de entrada del email */
    @ViewChild('resetEmail') emailModel!: NgModel;

    /** Objeto para la solicitud de restablecimiento */
    public request = { email: '' };

    /** Lista de errores de validación */
    public validationErrors: string[] = [];

    /** Mensaje de éxito */
    public successMessage: string = '';

    constructor(
        private router: Router,
        private authService: AuthService
    ) {}

    ngOnInit(): void {}

    /**
     * Deshabilita el botón de envío si el email no es válido.
     * @returns `true` si el formulario no debe enviarse.
     */
    isSubmitDisabled(): boolean {
        return !this.emailModel || !this.emailModel.valid;
    }

    /**
     * Maneja el envío del formulario.
     * @param event Evento de envío del formulario.
     */
    public submitForm(event: Event): void {
        event.preventDefault();
        this.validationErrors = [];
        this.successMessage = ''; 

        if (!this.emailModel || !this.emailModel.valid) {
            this.validationErrors.push('Please enter a valid email.');
            return;
        }

        this.authService.requestPasswordReset(this.request.email).subscribe({
            next: () => {
                this.successMessage = 'A password reset email has been sent. Check your inbox.';
            },
            error: (err: IExceptionResponse) => {
                console.error('Error:', err);
                this.validationErrors = Array.isArray(err.error?.exception)
                    ? err.error.exception
                    : [err.error?.exception || 'An unexpected error occurred.'];
            }
        });
    }
}