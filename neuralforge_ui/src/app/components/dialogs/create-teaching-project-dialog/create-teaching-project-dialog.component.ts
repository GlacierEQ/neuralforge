import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatNativeDateModule } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import {
  IProjectType,
  ISelectedDays,
  ITeachingProject,
} from "../../../interfaces";
import { AlertService } from "../../../services/alert.service";
import { TeachingProjectService } from "../../../services/teaching-project.service";
import {
  ProjectFormComponent,
  ProjectFormData,
} from "../../project-form/project-form.component";
import { WeekdayPickerComponent } from "../../weekday-picker/weekday-picker.component";

export interface TeachingProjectData extends ProjectFormData {
  selectedDays: ISelectedDays;
  dailyHours: number;
  startDate: Date;
  endDate: Date;
}

@Component({
  selector: "app-create-teaching-project-dialog",
  standalone: true,
  templateUrl: "./create-teaching-project-dialog.component.html",
  styleUrls: ["./create-teaching-project-dialog.component.scss"],
  imports: [
    CommonModule,
    MatDialogModule,
    ProjectFormComponent,
    WeekdayPickerComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
})
export class CreateTeachingProjectDialogComponent {
  private alertService = inject(AlertService);
  private teachingProjectService = inject(TeachingProjectService);

  minStartDate = new Date(); // Today's date
  minEndDate = new Date();

  teachingProject: TeachingProjectData = {
    name: "",
    description: "",
    selectedDays: {
      monday: false,
      tuesday: false,
      wednesday: false,
      thursday: false,
      friday: false,
      saturday: false,
      sunday: false,
    },
    dailyHours: 1,
    startDate: new Date(),
    endDate: new Date(new Date().setDate(new Date().getDate() + 7)), // At least 7 days from now
  };

  constructor(
    private dialogRef: MatDialogRef<CreateTeachingProjectDialogComponent>
  ) {
    // Set min end date to be 7 days after start date
    this.updateMinEndDate();
  }

  isFormValid(): boolean {
    return this.getSelectedDaysCount() > 0 && this.areDatesValid();
  }

  areDatesValid(): boolean {
    const start = new Date(this.teachingProject.startDate);
    const end = new Date(this.teachingProject.endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return start < end && diffDays >= 7;
  }

  onStartDateChange() {
    this.updateMinEndDate();

    // If the current end date is less than min end date, update it
    const currentEndDate = new Date(this.teachingProject.endDate);
    if (currentEndDate < this.minEndDate) {
      this.teachingProject.endDate = new Date(this.minEndDate);
    }
  }

  updateMinEndDate() {
    // Set min end date to be 7 days after start date
    const startDate = new Date(this.teachingProject.startDate);
    this.minEndDate = new Date(startDate);
    this.minEndDate.setDate(startDate.getDate() + 7);
  }

  onSubmit(projectData: ProjectFormData) {
    if (!this.isFormValid()) {
      let errorMessage = "";

      if (this.getSelectedDaysCount() === 0) {
        errorMessage = "Please select at least one day of the week";
      } else if (!this.areDatesValid()) {
        errorMessage =
          "Please ensure the end date is at least 7 days after the start date";
      }

      this.alertService.displayAlert("error", errorMessage, "center", "top", [
        "error-snackbar",
      ]);
      return;
    }

    const weeksCount = this.getWeeksCount();
    const hoursPerClass = 1;

    const teachingProjectData: ITeachingProject = {
      ...projectData,
      projectType: IProjectType.Teaching,
      selectedDays: this.teachingProject.selectedDays,
      dailyHours: this.teachingProject.dailyHours,
      weeksCount: weeksCount,
      hoursPerClass: hoursPerClass,
      createdAt: new Date(),
      materials: [],
      lastModifiedAt: null,
      id: "",
    };

    this.teachingProjectService.add(teachingProjectData).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
          "success",
          "Teaching project created successfully",
          "center",
          "top",
          ["success-snackbar"]
        );

        this.dialogRef.close(response);
      },
      error: (error: any) => {
        this.alertService.displayAlert(
          "error",
          error.error?.exception || "Failed to create teaching project",
          "center",
          "top",
          ["error-snackbar"]
        );

        console.error("Error creating project:", error);
      },
    });
  }

  getSelectedDaysCount(): number {
    return Object.values(this.teachingProject.selectedDays).filter((day) => day)
      .length;
  }

  getTotalHours(): number {
    const weeksCount = this.getWeeksCount();
    return (
      this.getSelectedDaysCount() * this.teachingProject.dailyHours * weeksCount
    );
  }

  getWeeksCount(): number {
    const start = new Date(this.teachingProject.startDate);
    const end = new Date(this.teachingProject.endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    const diffWeeks = diffDays / 7;
    return Math.round(diffWeeks * 10) / 10; // Round to 1 decimal place
  }

  onClose() {
    this.dialogRef.close();
  }
}
