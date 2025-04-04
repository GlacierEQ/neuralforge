import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { IExceptionResponse, IValidationRequest } from '../../../interfaces';
import { AuthService } from '../../../services/auth.service';
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatCardModule } from "@angular/material/card";

@Component({
    selector: 'app-email-verification',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        RouterLink,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatCardModule
    ],
    templateUrl: './verification.component.html',
    styleUrls: ['./verification.component.scss']
})
export class VerificationComponent implements OnInit {

    @ViewChild('email') emailModel!: NgModel;
    @ViewChild('verificationCode') verificationCodeModel!: NgModel;

    public validationRequest: IValidationRequest = {
        email: '',
        verificationCode: null
    };

    public hasEmailParam: boolean = false;
    public validationErrors!: string[];
    public successMessage: string = '';

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            if (params['email']) {
                this.validationRequest.email = params['email'];
                this.hasEmailParam = true;
            }
        });
    }

    isSubmitDisabled(): boolean {
        return (!this.hasEmailParam && !this.emailModel?.valid) || !this.verificationCodeModel?.valid;
    }

    public submitForm(event: Event): void {
        event.preventDefault();
        this.validationErrors = [];
        this.successMessage = '';

        if ((this.emailModel?.valid || this.hasEmailParam) && this.verificationCodeModel?.valid) {
            this.authService.verify(this.validationRequest).subscribe({
                next: () => {
                    this.successMessage = 'Your account has been activated. Redirecting to login...';
                    setTimeout(() => {
                        this.router.navigate(['/login']);
                    }, 3000);
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
}
