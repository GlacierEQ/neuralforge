import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { AlertService } from "../../../services/alert.service";
import {
  ProjectFormComponent,
  ProjectFormData,
} from "../../project-form/project-form.component";
import { WeekdayPickerComponent } from "../../weekday-picker/weekday-picker.component";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";

export interface StudyPlanData extends ProjectFormData {
  selectedDays: boolean[];
  dailyHours: number;
  weeksCount: number;
}

@Component({
  selector: "app-create-study-plan-dialog",
  standalone: true,
  templateUrl: "./create-study-plan-dialog.component.html",
  styleUrls: ["./create-study-plan-dialog.component.scss"],
  imports: [
    CommonModule, 
    MatDialogModule, 
    ProjectFormComponent, 
    WeekdayPickerComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule
  ],
})
export class CreateStudyPlanDialogComponent {
  private alertService = inject(AlertService);

  studyPlan: StudyPlanData = {
    name: "",
    description: "",
    selectedDays: [false, false, false, false, false, false, false], // Mon-Sun
    dailyHours: 1,
    weeksCount: 4,
  };

  constructor(private dialogRef: MatDialogRef<CreateStudyPlanDialogComponent>) {}

  onSubmit(projectData: ProjectFormData) {
    const studyPlanData: StudyPlanData = {
      ...projectData,
      selectedDays: this.studyPlan.selectedDays,
      dailyHours: this.studyPlan.dailyHours,
      weeksCount: this.studyPlan.weeksCount
    };

    this.alertService.displayAlert(
      "success",
      "Study plan created successfully",
      "center",
      "top",
      ["success-snackbar"]
    );

    this.dialogRef.close(studyPlanData);
  }

  getSelectedDaysCount(): number {
    return this.studyPlan.selectedDays.filter(day => day).length;
  }

  getTotalHours(): number {
    return this.getSelectedDaysCount() * this.studyPlan.dailyHours * this.studyPlan.weeksCount;
  }

  onClose() {
    this.dialogRef.close();
  }
}