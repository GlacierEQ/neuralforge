import { CommonModule } from "@angular/common";
import { Component, Inject, OnInit } from "@angular/core";
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
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { ProjectMaterialService } from "../../../../services/project-material.service";

@Component({
  selector: "app-upload-material-dialog",
  templateUrl: "./upload-material-dialog.component.html",
  styleUrls: ["./upload-material-dialog.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
})
export class UploadMaterialDialogComponent implements OnInit {
  uploadForm: FormGroup;
  selectedFile: File | null = null;
  isUploading = false;
  materialTypes = [
    { value: "file", label: "File" },
    { value: "hyperlink", label: "Hyperlink" },
  ];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UploadMaterialDialogComponent>,
    private projectMaterialService: ProjectMaterialService,
    @Inject(MAT_DIALOG_DATA) public data: { projectId: string }
  ) {
    this.uploadForm = this.fb.group({
      type: ["", Validators.required],
      description: [""],
      hyperlink: [""],
      file: [null],
    });
  }

  ngOnInit(): void {
    this.uploadForm.get("type")?.valueChanges.subscribe((type) => {
      const hyperlinkControl = this.uploadForm.get("hyperlink");
      const fileControl = this.uploadForm.get("file");
  
      if (type === "hyperlink") {
        hyperlinkControl?.setValidators([
          Validators.required,
          Validators.pattern(/^https?:\/\/.+\.(pdf|txt)$/i),
        ]);
        hyperlinkControl?.updateValueAndValidity();
        fileControl?.clearValidators();
        fileControl?.updateValueAndValidity();
      } else if (type === "file") {
        hyperlinkControl?.clearValidators();
        hyperlinkControl?.updateValueAndValidity();
        fileControl?.setValidators(Validators.required);
        fileControl?.updateValueAndValidity();
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const fileType = file.type;
      if (fileType !== "application/pdf" && fileType !== "text/plain") {
        alert("Only PDF and TXT files are allowed");
        return;
      }

      if (file.size > 10 * 1024 * 1024) {
        alert("File size must be less than 10MB");
        return;
      }

      this.selectedFile = file;
      this.uploadForm.patchValue({
        file: file,
      });
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file) {
      const fileType = file.type;
      if (fileType !== "application/pdf" && fileType !== "text/plain") {
        alert("Only PDF and TXT files are allowed");
        return;
      }

      if (file.size > 10 * 1024 * 1024) {
        alert("File size must be less than 10MB");
        return;
      }

      this.selectedFile = file;
      this.uploadForm.patchValue({
        file: file,
      });
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onSubmit(): void {
    if (this.uploadForm.valid) {
      if (this.uploadForm.value.type === "file") {
        if (!this.selectedFile) {
          alert("Please select a file to upload");
          return;
        }

        if (this.selectedFile.size === 0) {
          alert("Cannot upload an empty file");
          return;
        }
      }

      this.isUploading = true;
      const formData = new FormData();
      const formValue = this.uploadForm.value;

      formData.append("type", formValue.type);
      formData.append("description", formValue.description || "");
      formData.append("projectId", this.data.projectId);

      if (formValue.type === "file" && this.selectedFile) {
        formData.append("file", this.selectedFile, this.selectedFile.name);
      } else if (formValue.type === "hyperlink") {
        formData.append("hyperlink", formValue.hyperlink);
      }

      this.projectMaterialService.uploadMaterial(formData).subscribe({
        next: (response) => {
          this.dialogRef.close(response);
        },
        error: (error: any) => {
          console.error("Error uploading material:", error);
          this.isUploading = false;
          alert(
            "Failed to upload material: " +
              (error.error || error.message || "Unknown error")
          );
        },
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
