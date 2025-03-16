/**
 * VerificationComponent is responsible for handling email verification.
 * It validates user input and communicates with the authentication service
 * to verify the provided email and verification code.
 */
import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { IExceptionResponse, IValidationRequest } from '../../../interfaces';
import { AuthService } from '../../../services/auth.service';

@Component({
    selector: 'app-email-verification',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './verification.component.html',
    styleUrl: './verification.component.scss'
})
export class VerificationComponent implements OnInit {
    /** Reference to the email input field */
    @ViewChild('email') emailModel!: NgModel;

    /** Reference to the verification code input field */
    @ViewChild('verificationCode') verificationCodeModel!: NgModel;

    /** Object storing email and verification code for submission */
    public validationRequest: IValidationRequest = {
        email: '',
        verificationCode: null
    };

    /** Flag to determine if email is passed as a query parameter */
    public hasEmailParam: boolean = false;

    /** Stores validation errors returned by the API */
    public validationErrors!: [String?];

    /** Stores success messages to be displayed */
    public successMessage: string = '';

    /**
     * Constructor initializes required services.
     * @param route ActivatedRoute to access query parameters
     * @param router Router for navigation
     * @param authService AuthService to handle verification requests
     */
    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authService: AuthService
    ) {}

    /**
     * Lifecycle hook that runs on component initialization.
     * Retrieves email from query parameters if present.
     */
    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            if (params['email']) {
                this.validationRequest.email = params['email'];
                this.hasEmailParam = true;
            }
        });
    }

    /**
     * Determines whether the form submission should be disabled.
     * @returns boolean indicating whether the form is invalid
     */
    isSubmitDisabled(): boolean {
        return (!this.hasEmailParam && !this.emailModel?.valid) || !this.verificationCodeModel?.valid;
    }

    /**
     * Handles form submission by sending verification request.
     * @param event Form submit event
     */
    public submitForm(event: Event): void {
        event.preventDefault();
        this.validationErrors = [];
        this.successMessage = ''; // Reset success message

        console.log('Email:', this.validationRequest.email);
        console.log('Validation Code:', this.validationRequest.verificationCode);

        if ((this.emailModel.valid || this.hasEmailParam) && this.verificationCodeModel.valid) {
            this.authService.verify(this.validationRequest).subscribe({
                next: () => {
                    this.successMessage = 'Your account has been activated. Redirecting to login...';

                    // Wait for 3 seconds before redirecting
                    setTimeout(() => {
                        this.router.navigate(['/login']);
                    }, 3000);
                },
                error: (err: IExceptionResponse) => {
                    console.log(err);
                    this.validationErrors = Array.isArray(err.error.exception)
                        ? err.error.exception
                        : [err.error.exception];
                }
            });
        }
    }
}
