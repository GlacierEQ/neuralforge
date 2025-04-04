import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { AlertService } from "../../../services/alert.service";
import { DynamicContentService } from "../../../services/dinamicContent.service";
import { IDynamicContent } from "../../../interfaces";
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: "app-create-dynamic-content-dialog",
  standalone: true,
  templateUrl: "./create-dinamic-content-dialog.component.html",
  styleUrls: ["./create-dinamic-content-dialog.component.scss"],
  imports: [
    CommonModule,
    MatDialogModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
})
export class CreateDynamicContentDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<CreateDynamicContentDialogComponent>,
    private alertService: AlertService,
    private dynamicContentService: DynamicContentService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.dynamicContent.projectId = data.projectId;
    this.dynamicContent.email = data.email;
  }

  dynamicContent: IDynamicContent = {
    id: "",
    title: "",
    creationDate: new Date(),
    path: "",
    email: "",
    type: "",
    projectId: ""
  };

  selectedFile: File | null = null;
  loading = false;

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length) {
      this.selectedFile = event.target.files[0];
    }
  }

  onSubmit() {
    if (!this.selectedFile) {
      this.alertService.displayAlert(
        "error",
        "Please select a PDF file.",
        "center",
        "top",
        ["error-snackbar"]
      );
      return;
    }

    this.loading = true;

    this.dynamicContentService.generateSummary(
      this.selectedFile,
      this.dynamicContent.title,
      this.dynamicContent.email,
      this.dynamicContent.type,
      this.dynamicContent.projectId
    ).subscribe({
      next: (response: string) => {
        this.alertService.displayAlert(
          "success",
          "Dynamic content generated successfully",
          "center",
          "top",
          ["success-snackbar"]
        );
        this.dialogRef.close(response);
      },
      error: (error: any) => {
        this.alertService.displayAlert(
          "error",
          error.error?.exception || "Failed to generate dynamic content",
          "center",
          "top",
          ["error-snackbar"]
        );
        console.error("Error generating dynamic content:", error);
      }
    });
  }

  onClose() {
    this.dialogRef.close();
  }
}