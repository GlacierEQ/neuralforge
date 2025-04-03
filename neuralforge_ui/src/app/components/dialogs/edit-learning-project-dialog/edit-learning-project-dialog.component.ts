import { CommonModule } from "@angular/common";
import { Component, Inject, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { ILearningProject } from "../../../interfaces";
import { AlertService } from "../../../services/alert.service";
import { LearningProjectService } from "../../../services/learning-project.service";
import {
  ProjectFormComponent,
  ProjectFormData,
} from "../../project-form/project-form.component";

@Component({
  selector: "app-edit-learning-project-dialog",
  standalone: true,
  templateUrl: "./edit-learning-project-dialog.component.html",
  styleUrls: ["./edit-learning-project-dialog.component.scss"],
  imports: [
    CommonModule,
    MatDialogModule,
    ProjectFormComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
  ],
})
export class EditLearningProjectDialogComponent implements OnInit {
  learningProject!: ILearningProject;
  projectFormData: ProjectFormData = {
    name: "",
    description: "",
  };

  constructor(
    private dialogRef: MatDialogRef<EditLearningProjectDialogComponent>,
    private alertService: AlertService,
    private learningProjectService: LearningProjectService,
    @Inject(MAT_DIALOG_DATA) public data: ILearningProject
  ) {}

  ngOnInit(): void {
    this.learningProject = { ...this.data };
    this.projectFormData = {
      name: this.learningProject.name,
      description: this.learningProject.description || "", // Ensure description is never undefined
    };
  }

  onSubmit(projectData: ProjectFormData): void {
    const updatedProject: ILearningProject = {
      ...this.learningProject,
      name: projectData.name,
      description: projectData.description,
    };

    this.learningProjectService.update(updatedProject).subscribe({
      next: (response) => {
        this.alertService.displayAlert(
          "success",
          "Learning project updated successfully!",
          "center",
          "top",
          ["success-snackbar"]
        );
        this.dialogRef.close(response);
      },
      error: (err) => {
        console.error("Error updating project:", err);
        this.alertService.displayAlert(
          "error",
          err.error?.exception || "Failed to update learning project",
          "center",
          "top",
          ["error-snackbar"]
        );
      },
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
