import { CommonModule } from "@angular/common";
import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
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
import { ProgrammedGoalProjectService } from "../../../services/programmed-goal-project.service";
import { IProgrammedGoalProject } from "../../../interfaces";

@Component({
    selector: "app-edit-goal-project-dialog",
    standalone: true,
    templateUrl: "./edit-goal-project-dialog.component.html",
    styleUrls: ["./edit-goal-project-dialog.component.scss"],
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
        MatSelectModule,
    ],
})
export class EditGoalProjectDialogComponent implements OnInit {
    goalProject!: IProgrammedGoalProject;
    frequency: number = 3;
    isFrequencyValid(): boolean {
        return this.frequency === 7 || this.getSelectedDaysCount() === this.frequency;
    }

    constructor(
        private dialogRef: MatDialogRef<EditGoalProjectDialogComponent>,
        private alertService: AlertService,
        private programmedGoalProjectService: ProgrammedGoalProjectService,
        @Inject(MAT_DIALOG_DATA) public data: IProgrammedGoalProject
    ) {}

    ngOnInit(): void {
        this.goalProject = { ...this.data };
        this.frequency = this.getSelectedDaysCount();
    }

    onSubmit(projectData: ProjectFormData) {
        const updatedProject: IProgrammedGoalProject = {
            ...this.goalProject,
            ...projectData,
            deadline: this.goalProject.deadline,
            selectedDays: this.goalProject.selectedDays,
        };

        console.log("Submitting edited project:", updatedProject);

        this.programmedGoalProjectService.update(updatedProject).subscribe({
            next: (response) => {
                this.alertService.displayAlert(
                    "success",
                    "Project updated successfully!",
                    "center",
                    "top",
                    ["success-snackbar"]
                );
                this.dialogRef.close(response);
            },
            error: (err) => {
                this.alertService.displayAlert(
                    "error",
                    `Failed to update project: ${err.error.exception}`,
                    "center",
                    "top",
                    ["error-snackbar"]
                );
                console.error("Update error:", err);
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
            sunday: false,
        };

        if (this.frequency === 7) {
            Object.keys(this.goalProject.selectedDays).forEach((day) => {
                this.goalProject.selectedDays[day] = true;
            });
        }
    }

    getSelectedDaysCount(): number {
        return Object.values(this.goalProject.selectedDays).filter((day) => day).length;
    }

    onClose() {
        this.dialogRef.close();
    }
}
