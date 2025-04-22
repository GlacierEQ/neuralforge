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
import { MatInputModule } from "@angular/material/input";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { BehaviorSubject } from "rxjs";
import { ProjectMaterial } from "../../../../models/project-material.model";
import { AlertService } from "../../../../services/alert.service";
import { DynamicContentService } from "../../../../services/dynamic-content.service";
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

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
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: "./generate-content-dialog.component.html",
  styleUrls: ["./generate-content-dialog.component.scss"],
})
export class GenerateContentDialogComponent implements OnInit {
  form: FormGroup;
  isLoading = false;
  materials$ = new BehaviorSubject<ProjectMaterial[]>([]);
  languages = ['en', 'es', 'fr', 'de'];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<GenerateContentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private dynamicContentService: DynamicContentService,
    private alert: AlertService
  ) {
    this.form = this.fb.group({
      title: ["", [Validators.required]],
      type: ["SUMMARY", [Validators.required]],
      materialId: ["", [Validators.required]],
      language: ["en", [Validators.required]], 
    });
  }

  ngOnInit(): void {
    const filteredMaterials = this.data.materials.filter((m) =>
      (m.type === "file" && m.fileName?.toLowerCase().match(/\.(pdf|txt)$/)) ||
      (m.type === "hyperlink" && m.hyperlink?.toLowerCase().match(/\.(pdf|txt)$/))
    );
  
    this.materials$.next(filteredMaterials);
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.isLoading = true;
      const { materialId, title, type, language } = this.form.value;

      this.dynamicContentService
        .generateContent(
          this.data.projectId,
          materialId,
          title,
          type,
          language
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

  getDefaultNameForHyperlink(material: ProjectMaterial): string {
    if (material.type === 'hyperlink' && material.hyperlink) {
      // Eliminar los parámetros de la URL si existen (todo después del ?)
      const cleanUrl = material.hyperlink.split('?')[0];
  
      // Obtener el nombre del archivo, que es la última parte de la URL
      const parts = cleanUrl.split('/');
      return parts[parts.length - 1] || 'Unnamed Link';
    }
    return 'Unnamed';
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}