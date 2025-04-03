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
import {ProgrammedGoalProjectService} from "../../../services/programmed-goal-project.service";
import {IProgrammedGoalProject, ProjectTypeEnum} from "../../../interfaces";



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
  private programmedGoalProjectService = inject(ProgrammedGoalProjectService)
  public frequency:number = 3;

  goalProject: IProgrammedGoalProject = {
    createdAt: null,
    notify: false,
    projectType: ProjectTypeEnum.PROGRAMMED_GOAL,
    name: "",
    description: "",
    deadline: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // Default to 30 days from now
    selectedDays: {
      monday: false,
      tuesday: false,
      wednesday: false,
      thursday: false,
      friday: false,
      saturday: false,
      sunday: false
    }

  };

  frequencies = [1, 2, 3, 4, 5, 6, 7];

  constructor(private dialogRef: MatDialogRef<CreateGoalProjectDialogComponent>) {}

  onSubmit(projectData: ProjectFormData) {

    const goalData: IProgrammedGoalProject = {
      createdAt: null,
      notify: false,
      projectType: ProjectTypeEnum.PROGRAMMED_GOAL,
      ...projectData, // This includes name and description from the form
      deadline: this.goalProject.deadline,
      selectedDays: this.goalProject.selectedDays

    };

    console.log(goalData);

    this.programmedGoalProjectService.add(goalData).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
            "success",
            "Programmed goal project created successfully",
            "center",
            "top",
            ["success-snackbar"]
        );

        this.dialogRef.close(response);
      },
      error: (error: any) => {
        this.alertService.displayAlert(
            "error",
            error.error?.exception || "Failed to create programmed goal project",
            "center",
            "top",
            ["error-snackbar"]
        );

        console.error("Error creating project:", error);
      },
    });
  }

  onFrequencyChange(): void {
    this.goalProject.selectedDays = {
      monday: false,
      tuesday: false,
      wednesday: false,
      thursday: false,
      friday: false,
      saturday: false,
      sunday: false
    };

    if (this.frequency === 7) {
      Object.keys(this.goalProject.selectedDays).forEach(day => {
        this.goalProject.selectedDays[day] = true;
      });
    }
  }

  isFrequencyValid(): boolean {
    return this.frequency === 7 || this.getSelectedDaysCount() === this.frequency;
  }

  getSelectedDaysCount(): number {
    return Object.values(this.goalProject.selectedDays).filter(day => day).length;
  }


  onClose() {
    this.dialogRef.close();
  }
}