import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { IProgrammedGoalProject, IDynamicContentSection, IDynamicContent } from '../../../interfaces';
import { ProgrammedGoalProjectService } from '../../../services/programmed-goal-project.service';
import { CommonModule } from "@angular/common";
import { AuthService } from '../../../services/auth.service';

import {
    NgIf,
    NgFor,
    AsyncPipe,
    DatePipe, NgClass,
} from '@angular/common';
import {
    MatTabsModule
} from '@angular/material/tabs';
import {
    MatIconModule
} from '@angular/material/icon';
import {
    MatButtonModule
} from '@angular/material/button';
import {
    MatSlideToggleModule
} from '@angular/material/slide-toggle';
import { MatCardModule } from '@angular/material/card';
import {DeadlineCalendarComponent} from "../../../components/deadline-calendar/deadline-calendar.component";
import {AlertService} from "../../../services/alert.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../../components/dialogs/confirm-dialog/confirm-dialog.component";
import {EditGoalProjectDialogComponent} from "../../../components/dialogs/edit-goal-project-dialog/edit-goal-project-dialog.component";
import { CreateDynamicContentDialogComponent } from '../../../components/dialogs/create-dinamic-content-dialog/create-dinamic-content-dialog.component';


@Component({
    selector: 'app-programmed-goal-project',
    standalone: true,
    templateUrl: './programmed-goal-project.component.html',
    styleUrls: ['./programmed-goal-project.component.scss'],
    imports: [
        NgIf,
        NgFor,
        AsyncPipe,
        DatePipe,
        MatTabsModule,
        MatIconModule,
        MatButtonModule,
        MatSlideToggleModule,
        MatCardModule,
        NgClass,
        CommonModule,
        DeadlineCalendarComponent
         
    ]
})
export class ProgrammedGoalProjectComponent implements OnInit {
    projectId: string = '';
    project?: IProgrammedGoalProject;
    isLoading = true;
    hasError = false;
    projectLoaded = false;
    
    dynamicContentSection: IDynamicContentSection = {
        title: "Dynamic Content",
        buttonText: "Add Dynamic Content",
        buttonAction: this.openDynamicContentForm.bind(this),
        isLoading: true,
        cards: [],
      };

    calendarStartDate = new Date();
    today = new Date();

    constructor(
        private route: ActivatedRoute,
        private projectService: ProgrammedGoalProjectService,
        private cdr: ChangeDetectorRef,
        private alert: AlertService,
        private router: Router,
        private dialog: MatDialog,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.projectId = this.route.snapshot.paramMap.get('projectId') ?? '';
        if (this.projectId) {
            this.projectService.getById(this.projectId).subscribe({
                next: (project) => {
                    this.project = project;
                    this.projectLoaded = true;
                    this.isLoading = false;
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    console.error('Error fetching project:', err);
                    this.hasError = true;
                    this.isLoading = false;
                    this.projectLoaded = false;

                    this.alert.displayAlert(
                        'error',
                        'Project not found or could not be loaded.',
                        'center',
                        'top',
                        ['error-snackbar']
                    );

                    this.router.navigate(['/app/dashboard']);
                }

            });
        }
    }

    getDaysLeft(): number {
        if (!this.project?.deadline) return 0;
        const now = new Date();
        const deadline = new Date(this.project.deadline);
        const diffTime = deadline.getTime() - now.getTime();
        return Math.max(Math.ceil(diffTime / (1000 * 60 * 60 * 24)), 0);
    }

    onToggleNotify() {
        if (!this.project) return;

        const previousState = this.project.notify;

        this.project.notify = !previousState;

        this.projectService.toggleNotifications(this.projectId).subscribe({
            next: (updatedProject) => {
                this.project = updatedProject;
            },
            error: (err) => {
                console.error('Error toggling notifications:', err);

                this.project!.notify = previousState;

                this.alert.displayAlert(
                    'error',
                    err.error.exception,
                    'center',
                    'top',
                    ['error-snackbar']
                );

                this.cdr.detectChanges();
            }
        });
    }

    onDeleteProject() {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '400px',
            data: {
                title: 'Confirm Deletion',
                message: 'Are you sure you want to delete this project? This action cannot be undone.',
                confirmText: 'Delete',
                cancelText: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.projectService.delete(this.projectId).subscribe({
                    next: () => {
                        this.alert.displayAlert(
                            'success',
                            `Project "${this.project?.name}" deleted successfully.`,
                            'center',
                            'top',
                            ['success-snackbar']
                        );
                        this.router.navigate(['/app/dashboard']);
                    },
                    error: (err) => {
                        console.error('Delete error:', err);
                        this.alert.displayAlert(
                            'error',
                            'Failed to delete the project.',
                            'center',
                            'top',
                            ['error-snackbar']
                        );
                    }
                });
            }
        });
    }

    editProject() {
        if (!this.project) return;

        const dialogRef = this.dialog.open(EditGoalProjectDialogComponent, {
            width: '800px',
            data: this.project
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
               this.project = result;
            }
        });
    }
    
    openDynamicContentForm() {
      
        this.authService.getCurrentUser().subscribe(user => {
          if (user) {
            const dialogRef = this.dialog.open(CreateDynamicContentDialogComponent, {
              width: '800px',
              data: { 
                projectId: this.projectId,
                email: user.email 
              }
            });
      
            dialogRef.afterClosed().subscribe((result) => {
              if (result) {
                const newDynamicContent: IDynamicContent = {
                  id: result.id,
                  title: result.title,
                  creationDate: result.creationDate,
                  path: result.path,
                  email: user.email, 
                  type: result.type,
                  projectId: this.projectId
                };
      
                this.dynamicContentSection.cards.push(newDynamicContent);
              }
            });
          }
        });
      }

}