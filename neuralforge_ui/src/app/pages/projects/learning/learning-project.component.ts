import { DatePipe, NgIf } from "@angular/common";
import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { ConfirmDialogComponent } from "../../../components/dialogs/confirm-dialog/confirm-dialog.component";
import { EditLearningProjectDialogComponent } from "../../../components/dialogs/edit-learning-project-dialog/edit-learning-project-dialog.component";
import { ProjectDetailsComponent } from "../../../components/project-details/project-details.component";
import { QuizListComponent } from "../../../components/quiz/quiz-list/quiz-list.component";
import { ILearningProject } from "../../../interfaces";
import { AlertService } from "../../../services/alert.service";
import { LearningProjectService } from "../../../services/learning-project.service";

@Component({
  selector: "app-learning-project",
  standalone: true,
  templateUrl: "./learning-project.component.html",
  styleUrls: ["./learning-project.component.scss"],
  imports: [
    NgIf,
    DatePipe,
    MatTabsModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    RouterLink,
    ProjectDetailsComponent,
    QuizListComponent,
  ],
})
export class LearningProjectComponent implements OnInit {
  projectId: string = "";
  project: ILearningProject | null = null;
  isLoading = true;
  hasError = false;
  errorMessage = "Unable to load project details.";

  constructor(
    private route: ActivatedRoute,
    private projectService: LearningProjectService,
    private cdr: ChangeDetectorRef,
    private alert: AlertService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get("projectId") ?? "";

    if (this.projectId) {
      this.loadProject();
    } else {
      this.handleError("No project ID found");
    }
  }

  private loadProject(): void {
    this.projectService.getById(this.projectId).subscribe({
      next: (project) => {
        if (project) {
          this.project = project;
          this.isLoading = false;
          this.cdr.detectChanges();
        } else {
          this.handleError("Project data is null or undefined");
        }
      },
      error: (err) => {
        console.error("Error fetching project:", err);
        this.handleError(err.message || "Failed to load project details");
      },
    });
  }

  private handleError(message: string): void {
    this.hasError = true;
    this.isLoading = false;
    this.errorMessage = message;

    this.alert.displayAlert(
      "error",
      "Learning project not found or could not be loaded.",
      "center",
      "top",
      ["error-snackbar"]
    );
  }

  onDeleteProject(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "400px",
      data: {
        title: "Confirm Deletion",
        message:
          "Are you sure you want to delete this learning project? This action cannot be undone.",
        confirmText: "Delete",
        cancelText: "Cancel",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteProject();
      }
    });
  }

  private deleteProject(): void {
    this.projectService.delete(this.projectId).subscribe({
      next: () => {
        this.alert.displayAlert(
          "success",
          `Learning project "${this.project?.name}" deleted successfully.`,
          "center",
          "top",
          ["success-snackbar"]
        );
        this.router.navigate(["/app/dashboard"]);
      },
      error: (err) => {
        console.error("Delete error:", err);
        this.alert.displayAlert(
          "error",
          "Failed to delete the learning project.",
          "center",
          "top",
          ["error-snackbar"]
        );
      },
    });
  }

  editProject(): void {
    if (!this.project) return;

    const dialogRef = this.dialog.open(EditLearningProjectDialogComponent, {
      width: "800px",
      data: this.project,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.project = result;
        this.cdr.detectChanges();
      }
    });
  }

  goToMaterials(): void {
    const materialsTabIndex = 1;
    const tabGroup = document.querySelector("mat-tab-group");
    if (tabGroup) {
      const tabHeader = tabGroup.querySelector(
        `.mat-mdc-tab:nth-child(${materialsTabIndex + 1})`
      );
      if (tabHeader) {
        (tabHeader as HTMLElement).click();
      }
    }
  }
}
