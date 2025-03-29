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
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatSelectModule } from "@angular/material/select";

export interface GoalProjectData extends ProjectFormData {
  deadline: Date;
  frequency: number;
  selectedDays: boolean[];
}

@Component({
  selector: "app-create-goal-project-dialog",
  standalone: true,
  templateUrl: "./create-goal-project-dialog.component.html",
  styleUrls: ["./create-goal-project-dialog.component.scss"],
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
    MatSelectModule
  ],
})
export class CreateGoalProjectDialogComponent {
  private alertService = inject(AlertService);

  goalProject: GoalProjectData = {
    name: "",
    description: "",
    deadline: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // Default to 30 days from now
    frequency: 3,
    selectedDays: [false, false, false, false, false, false, false], // Mon-Sun
  };

  frequencies = [1, 2, 3, 4, 5, 6, 7];

  constructor(private dialogRef: MatDialogRef<CreateGoalProjectDialogComponent>) {}

  onSubmit(projectData: ProjectFormData) {
    const goalData: GoalProjectData = {
      ...projectData, // This includes name and description from the form
      deadline: this.goalProject.deadline,
      frequency: this.goalProject.frequency,
      selectedDays: this.goalProject.selectedDays
    };

    this.alertService.displayAlert(
      "success",
      "Programmed Goal Project created successfully",
      "center",
      "top",
      ["success-snackbar"]
    );

    this.dialogRef.close(goalData);
  }

  onFrequencyChange(): void {
    // Reset selected days
    this.goalProject.selectedDays = [false, false, false, false, false, false, false];
  }

  getSelectedDaysCount(): number {
    return this.goalProject.selectedDays.filter(day => day).length;
  }

  onClose() {
    this.dialogRef.close();
  }
}