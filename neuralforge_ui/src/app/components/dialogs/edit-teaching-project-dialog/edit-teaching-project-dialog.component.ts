import { CommonModule } from "@angular/common";
import { Component, Inject, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatNativeDateModule } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { ITeachingProject } from "../../../interfaces";

@Component({
  selector: "app-edit-teaching-project-dialog",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
  ],
  templateUrl: "./edit-teaching-project-dialog.component.html",
  styleUrls: ["./edit-teaching-project-dialog.component.scss"],
})
export class EditTeachingProjectDialogComponent implements OnInit {
  form: FormGroup;
  selectedDays = [
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday",
  ];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditTeachingProjectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { project: ITeachingProject }
  ) {
    this.form = this.fb.group({
      name: ["", [Validators.required]],
      description: ["", [Validators.required]],
      dailyHours: ["", [Validators.required, Validators.min(1)]],
      weeksCount: ["", [Validators.required, Validators.min(1)]],
      selectedDays: [[], [Validators.required]],
      materials: [[]],
    });
  }

  ngOnInit() {
    if (this.data.project) {
      this.form.patchValue({
        name: this.data.project.name,
        description: this.data.project.description,
        dailyHours: this.data.project.dailyHours,
        weeksCount: this.data.project.weeksCount,
        selectedDays: this.data.project.selectedDays,
        materials: this.data.project.materials || [],
      });
    }
  }

  onSubmit() {
    if (this.form.valid) {
      const updatedProject: ITeachingProject = {
        ...this.data.project,
        ...this.form.value,
      };
      this.dialogRef.close(updatedProject);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  addMaterial() {
    const materials = this.form.get("materials")?.value || [];
    materials.push("");
    this.form.patchValue({ materials });
  }

  removeMaterial(index: number) {
    const materials = this.form.get("materials")?.value || [];
    materials.splice(index, 1);
    this.form.patchValue({ materials });
  }
}
