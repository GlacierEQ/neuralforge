import { Component, OnInit, inject } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { VirtualClassService } from '../../services/virtual-class.service';
import { IVirtualClass } from '../../interfaces';
import { AlertService } from '../../services/alert.service';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { MatIcon } from '@angular/material/icon';
import {Router, RouterLink} from '@angular/router';
import {MatPaginator} from "@angular/material/paginator";

@Component({
    selector: 'app-virtual-class-dashboard',
    templateUrl: './virtual-class-dashboard.component.html',
    styleUrls: ['./virtual-class-dashboard.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatCardModule,
        MatTab,
        MatTabGroup,
        MatIcon,
        RouterLink,
        MatPaginator,
        FormsModule
    ]
})
export class VirtualClassDashboardComponent implements OnInit {
    createForm!: FormGroup;
    isLoading = false;
    selectedTab: 'create' | 'my' | 'all' = 'create';
    myClasses: IVirtualClass[] = [];
    allClasses: IVirtualClass[] = [];
    paginatedMyClasses: IVirtualClass[] = [];
    paginatedAllClasses: IVirtualClass[] = [];
    isLoadingMy = true;
    isLoadingAll = true;
    mySearchTerm: string = '';
    allSearchTerm: string = '';

    filteredMyClasses: IVirtualClass[] = [];
    filteredAllClasses: IVirtualClass[] = [];
    private alertService = inject(AlertService);

    filterClasses(type: 'my' | 'all'): void {
        const searchTerm = type === 'my' ? this.mySearchTerm.toLowerCase() : this.allSearchTerm.toLowerCase();
        const sourceArray = type === 'my' ? this.myClasses : this.allClasses;
        const filteredArray = sourceArray.filter(vc =>
            vc.title.toLowerCase().includes(searchTerm) ||
            vc.description.toLowerCase().includes(searchTerm)
        );

        if (type === 'my') {
            this.filteredMyClasses = filteredArray;
            this.paginateClasses(this.filteredMyClasses, 'my');
        } else {
            this.filteredAllClasses = filteredArray;
            this.paginateClasses(this.filteredAllClasses, 'all');
        }
    }


    constructor(
        private fb: FormBuilder,
        private virtualClassService: VirtualClassService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.createForm = this.fb.group({
            title: ['', [Validators.required, Validators.minLength(3)]],
            description: ['', [Validators.required, Validators.minLength(5)]]
        });

        this.loadMyClasses();
        this.loadAllClasses();
    }

    onSubmit(): void {
        if (this.createForm.invalid) return;

        this.isLoading = true;
        const virtualClass: IVirtualClass = this.createForm.value;

        this.virtualClassService.create(virtualClass).subscribe({
            next: () => {
                this.isLoading = false;
                this.alertService.displayAlert(
                    'success',
                    'Virtual class created successfully!',
                    'center',
                    'top',
                    ['success-snackbar']
                );
                this.createForm.reset();
                this.selectedTab = 'my'; // Switch to "My Virtual Classes"
                this.loadMyClasses();    // Reload the list
                this.loadAllClasses();   // Optionally reload "all" too
            },
            error: (error) => {
                this.isLoading = false;
                this.alertService.displayAlert(
                    'error',
                    error.error?.exception || 'Failed to create virtual class',
                    'center',
                    'top',
                    ['error-snackbar']
                );
                console.error('Error creating virtual class:', error);
            }
        });
    }


    loadMyClasses(): void {
        this.virtualClassService.getMine().subscribe({
            next: (data) => {
                this.myClasses = data;
                this.paginateClasses(this.myClasses, 'my');
                console.log(this.myClasses)
            },
            error: (error) => {
                console.error('Error loading my classes', error);
                this.isLoadingMy = false;
            }
        });
    }

    loadAllClasses(): void {
        this.virtualClassService.getAll().subscribe({
            next: (data) => {
                this.allClasses = data;
                this.paginateClasses(this.allClasses, 'all');
            },
            error: (error) => {
                console.error('Error loading all classes', error);
                this.isLoadingAll = false;
            }
        });
    }

    paginateClasses(classes: IVirtualClass[], type: 'my' | 'all', event?: any): void {
        const maxPerPage = 5;
        const startIndex = event ? event.pageIndex * maxPerPage : 0;
        const endIndex = startIndex + maxPerPage;

        if (type === 'my') {
            this.paginatedMyClasses = classes.slice(startIndex, endIndex);
            this.isLoadingMy = false;
        } else if (type === 'all') {
            this.paginatedAllClasses = classes.slice(startIndex, endIndex);
            this.isLoadingAll = false;
        }
    }

}
