import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import {NgForOf, NgIf} from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import {ProjectDetailsComponent} from "../../components/project-details/project-details.component";
import {IVirtualClass} from "../../interfaces";
import {VirtualClassService} from "../../services/virtual-class.service";
import {AlertService} from "../../services/alert.service";
import {ConfirmDialogComponent} from "../../components/dialogs/confirm-dialog/confirm-dialog.component";

@Component({
    selector: 'app-virtual-class',
    standalone: true,
    templateUrl: './virtual-class.component.html',
    styleUrls: ['./virtual-class.component.scss'],
    imports: [
        NgIf,
        MatCardModule,
        MatIconModule,
        ProjectDetailsComponent,
        NgForOf
    ],
})
export class VirtualClass implements OnInit {
    classId: string = '';
    virtualClass: IVirtualClass | null = null;
    isLoading = true;
    hasError = false;
    errorMessage = '';

    constructor(
        private route: ActivatedRoute,
        private service: VirtualClassService,
        private alert: AlertService,
        private router: Router,
        private cdr: ChangeDetectorRef,
        private dialog: MatDialog
    ) {}

    ngOnInit(): void {
        this.classId = this.route.snapshot.paramMap.get('classId') ?? '';
        if (this.classId) {
            this.service.getById(this.classId).subscribe({
                next: (vc) => {
                    console.log(vc)

                    this.virtualClass = vc;
                    this.isLoading = false;
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    this.hasError = true;
                    this.errorMessage = err?.error?.exception || 'Failed to load class.';
                    this.isLoading = false;
                    this.alert.displayAlert('error', this.errorMessage, 'center', 'top');
                    this.router.navigate(['/app/dashboard']);
                },
            });
        }
    }

    onDelete() {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '400px',
            data: {
                title: 'Delete Class?',
                message: 'Are you sure you want to delete this virtual class?',
                confirmText: 'Delete',
                cancelText: 'Cancel',
            },
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.service.delete(this.classId).subscribe({
                    next: () => {
                        this.alert.displayAlert('success', 'Class deleted successfully', 'center', 'top');
                        this.router.navigate(['/app/dashboard']);
                    },
                    error: () => {
                        this.alert.displayAlert('error', 'Failed to delete the class.', 'center', 'top');
                    },
                });
            }
        });
    }

    editClass() {
        // Open dialog for editing (to be implemented)
    }
}
