import { CommonModule, DatePipe, NgIf } from "@angular/common";
import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTabsModule } from "@angular/material/tabs";
import { ActivatedRoute, Router } from "@angular/router";
import { ConfirmDialogComponent } from "../../../components/dialogs/confirm-dialog/confirm-dialog.component";
import { EditTeachingProjectDialogComponent } from "../../../components/dialogs/edit-teaching-project-dialog/edit-teaching-project-dialog.component";
import { ITeachingProject } from "../../../interfaces";
import { AlertService } from "../../../services/alert.service";
import { TeachingProjectService } from "../../../services/teaching-project.service";

@Component({
  selector: "app-teaching-project",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    DatePipe,
    NgIf,
  ],
  templateUrl: "./teaching-project.component.html",
  styleUrls: ["./teaching-project.component.scss"],
})
export class TeachingProjectComponent implements OnInit {
  project: ITeachingProject | null = null;
  isLoading = true;
  hasError = false;
  errorMessage = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teachingProjectService: TeachingProjectService,
    private dialog: MatDialog,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get("projectId");
    if (projectId) {
      this.loadProject(projectId);
    }
  }

  private loadProject(id: string) {
    this.isLoading = true;
    this.hasError = false;
    this.errorMessage = "";

    this.teachingProjectService.getById(id).subscribe({
      next: (project) => {
        this.project = project;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error("Error loading project:", error);
        this.hasError = true;
        this.errorMessage = "Failed to load project. Please try again later.";
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  editProject() {
    if (!this.project) return;

    const dialogRef = this.dialog.open(EditTeachingProjectDialogComponent, {
      data: { project: this.project },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProject(this.project!.id!);
      }
    });
  }

  onDeleteProject() {
    if (!this.project) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: "Delete Project",
        message: "Are you sure you want to delete this project?",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.teachingProjectService.delete(this.project!.id!).subscribe({
          next: () => {
            this.alertService.displayAlert(
              "success",
              "Project deleted successfully",
              "center",
              "top",
              ["success-snackbar"]
            );
            this.router.navigate(["/app/dashboard"]);
          },
          error: (error) => {
            this.alertService.displayAlert(
              "error",
              "Failed to delete project",
              "center",
              "top",
              ["error-snackbar"]
            );
            console.error("Error deleting project:", error);
          },
        });
      }
    });
  }

  getSelectedDays(): string {
    if (!this.project?.selectedDays) return "No days selected";

    const days = Object.entries(this.project.selectedDays)
      .filter(([_, selected]) => selected)
      .map(([day]) => day.charAt(0).toUpperCase() + day.slice(1));

    return days.length > 0 ? days.join(", ") : "No days selected";
  }
}
