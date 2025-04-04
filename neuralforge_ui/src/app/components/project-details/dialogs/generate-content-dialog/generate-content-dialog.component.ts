import { CommonModule } from "@angular/common";
import { Component, Inject } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { ProjectMaterial } from "../../../../models/project-material.model";
import { AlertService } from "../../../../services/alert.service";
import { DynamicContentService } from "../../../../services/dynamic-content.service";

interface DialogData {
  projectId: string;
  materials: ProjectMaterial[];
}

@Component({
  selector: "app-generate-content-dialog",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: "./generate-content-dialog.component.html",
  styleUrls: ["./generate-content-dialog.component.scss"],
})
export class GenerateContentDialogComponent {
  form: FormGroup;
  isLoading = false;
  materials: ProjectMaterial[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<GenerateContentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private dynamicContentService: DynamicContentService,
    private alert: AlertService
  ) {
    this.materials = data.materials
      ? data.materials.filter((m) => m.type === "file")
      : [];
    this.form = this.fb.group({
      title: ["", [Validators.required]],
      type: ["SUMMARY", [Validators.required]],
      materialId: ["", [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.isLoading = true;
      const formData = this.form.value;

      this.dynamicContentService
        .generateContent(
          this.data.projectId,
          formData.materialId,
          formData.title,
          formData.type
        )
        .subscribe({
          next: () => {
            this.alert.displayAlert(
              "success",
              "Content generated successfully",
              "center",
              "top"
            );
            this.dialogRef.close(true);
          },
          error: (err: any) => {
            console.error("Error generating content:", err);
            this.alert.displayAlert(
              "error",
              "Failed to generate content",
              "center",
              "top"
            );
            this.isLoading = false;
          },
        });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
