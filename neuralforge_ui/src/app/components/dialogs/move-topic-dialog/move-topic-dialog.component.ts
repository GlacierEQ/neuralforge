import { CommonModule } from "@angular/common";
import { Component, Inject } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { IClassSession, ICourseTopic, ICourseWeek } from "../../../interfaces";

@Component({
  selector: "app-move-topic-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
  ],
  templateUrl: "./move-topic-dialog.component.html",
  styleUrls: ["./move-topic-dialog.component.scss"],
})
export class MoveTopicDialogComponent {
  selectedWeekNumber: number;
  selectedSessionId: string;

  constructor(
    public dialogRef: MatDialogRef<MoveTopicDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      topic: ICourseTopic;
      currentWeekNumber: number;
      currentSessionId: string;
      weeks: ICourseWeek[];
      projectStartDate?: string | Date;
    }
  ) {
    this.selectedWeekNumber = data.currentWeekNumber;
    this.selectedSessionId = data.currentSessionId;
  }

  getAvailableSessions(): IClassSession[] {
    const selectedWeek = this.data.weeks.find(
      (week) => week.weekNumber === this.selectedWeekNumber
    );

    if (!selectedWeek) return [];

    return [...selectedWeek.classSessions].sort((a, b) => {
      const dateA = this.getSessionDate(this.selectedWeekNumber, a.dayOfWeek);
      const dateB = this.getSessionDate(this.selectedWeekNumber, b.dayOfWeek);

      if (!dateA || !dateB) {
        const dayOrder = {
          MONDAY: 1,
          TUESDAY: 2,
          WEDNESDAY: 3,
          THURSDAY: 4,
          FRIDAY: 5,
          SATURDAY: 6,
          SUNDAY: 7,
        };

        return (
          dayOrder[a.dayOfWeek as keyof typeof dayOrder] -
          dayOrder[b.dayOfWeek as keyof typeof dayOrder]
        );
      }

      return dateA.getTime() - dateB.getTime();
    });
  }

  getDayOfWeekName(dayOfWeek: string): string {
    const dayMapping: { [key: string]: string } = {
      MONDAY: "Monday",
      TUESDAY: "Tuesday",
      WEDNESDAY: "Wednesday",
      THURSDAY: "Thursday",
      FRIDAY: "Friday",
      SATURDAY: "Saturday",
      SUNDAY: "Sunday",
    };

    return dayMapping[dayOfWeek] || dayOfWeek;
  }

  getSessionDate(weekNumber: number, dayOfWeek: string): Date | null {
    if (!this.data.projectStartDate) return null;

    const projectStartDate = new Date(this.data.projectStartDate);

    const dayMapping: { [key: string]: number } = {
      MONDAY: 1,
      TUESDAY: 2,
      WEDNESDAY: 3,
      THURSDAY: 4,
      FRIDAY: 5,
      SATURDAY: 6,
      SUNDAY: 0,
    };

    const startDayOfWeek = projectStartDate.getDay();
    const targetDayOfWeek = dayMapping[dayOfWeek];
    if (targetDayOfWeek === undefined) return null;

    let daysToAdd = (weekNumber - 1) * 7;
    let daysDiff = targetDayOfWeek - startDayOfWeek;
    daysToAdd += daysDiff;

    projectStartDate.setDate(projectStartDate.getDate() + daysToAdd);

    return projectStartDate;
  }

  onSubmit() {
    this.dialogRef.close({
      topic: this.data.topic,
      sourceWeekNumber: this.data.currentWeekNumber,
      sourceSessionId: this.data.currentSessionId,
      targetWeekNumber: this.selectedWeekNumber,
      targetSessionId: this.selectedSessionId,
    });
  }
}
