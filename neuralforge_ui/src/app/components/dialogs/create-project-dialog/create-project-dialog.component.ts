import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { AlertService } from "../../../services/alert.service";
import { LearningProjectService } from "../../../services/learning-project.service";
import {
  ProjectFormComponent,
  ProjectFormData,
} from "../../project-form/project-form.component";

@Component({
  selector: "app-create-project-dialog",
  standalone: true,
  templateUrl: "./create-project-dialog.component.html",
  styleUrls: ["./create-project-dialog.component.scss"],
  imports: [CommonModule, MatDialogModule, ProjectFormComponent],
})
export class CreateProjectDialogComponent {
  private learningProjectService = inject(LearningProjectService);
  private alertService = inject(AlertService);

  project: ProjectFormData = {
    name: "",
    description: "",
  };

  constructor(private dialogRef: MatDialogRef<CreateProjectDialogComponent>) {}

  onSubmit(projectData: ProjectFormData) {
    this.learningProjectService.add(projectData).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
          "success",
          "Learning project created successfully",
          "center",
          "top",
          ["success-snackbar"]
        );

        this.dialogRef.close(response);
      },
      error: (error: any) => {
        this.alertService.displayAlert(
          "error",
          error.error?.exception || "Failed to create learning project",
          "center",
          "top",
          ["error-snackbar"]
        );

        console.error("Error creating project:", error);
      },
    });
  }

  onClose() {
    this.dialogRef.close();
  }
}
