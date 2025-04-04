import { CommonModule, DatePipe } from "@angular/common";
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTooltipModule } from "@angular/material/tooltip";
import { Subscription } from "rxjs";
import {
  IClassSession,
  ICourseTopic,
  ICourseWeek,
  ITeachingProject,
} from "../../interfaces";
import { AlertService } from "../../services/alert.service";
import { ProjectMaterialService } from "../../services/project-material.service";
import { TeachingProjectService } from "../../services/teaching-project.service";
import { MoveTopicDialogComponent } from "../dialogs/move-topic-dialog/move-topic-dialog.component";

interface IWeek {
  weekNumber: number;
  startDate: Date;
  endDate: Date;
  days: {
    date: Date;
    isTeachingDay: boolean;
    topics: string[];
  }[];
}

@Component({
  selector: "app-teaching-calendar",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    DatePipe,
  ],
  templateUrl: "./teaching-calendar.component.html",
  styleUrls: ["./teaching-calendar.component.scss"],
})
export class TeachingCalendarComponent implements OnInit, OnDestroy {
  @Input() project: ITeachingProject | null = null;
  @Input() calendarStartDate: Date = new Date();
  @Output() calendarStartDateChange = new EventEmitter<Date>();

  weeks: IWeek[] = [];
  isGeneratingSchedule = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private teachingProjectService: TeachingProjectService,
    private alertService: AlertService,
    private dialog: MatDialog,
    private projectMaterialService: ProjectMaterialService
  ) {}

  ngOnInit() {
    this.generateWeeks();

    if (this.project?.id) {
      this.subscriptions.push(
        this.projectMaterialService.materialUpdates$.subscribe((update) => {
          if (update && update.projectId === this.project?.id) {
            this.teachingProjectService
              .getById(this.project.id)
              .subscribe((updatedProject) => {
                if (this.project) {
                  this.project.materials = updatedProject.materials;
                }
              });
          }
        })
      );
    }
  }

  ngOnChanges() {
    this.generateWeeks();
  }

  ngOnDestroy() {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  hasSchedule(): boolean {
    return !!this.project?.weeks && this.project.weeks.length > 0;
  }

  hasMaterials(): boolean {
    return !!this.project?.materials && this.project.materials.length > 0;
  }

  generateSchedule() {
    if (!this.project?.id) return;

    if (!this.hasMaterials()) {
      this.alertService.displayAlert(
        "warning",
        "Please add materials to your project before generating a schedule",
        "center",
        "top",
        ["warning-snackbar"]
      );
      return;
    }

    this.isGeneratingSchedule = true;

    this.teachingProjectService.generateSchedule(this.project.id).subscribe({
      next: (updatedProject) => {
        if (this.project) {
          this.project = updatedProject;
          this.generateWeeks();
        }
        this.isGeneratingSchedule = false;
        this.alertService.displayAlert(
          "success",
          "Schedule generated successfully",
          "center",
          "top",
          ["success-snackbar"]
        );
      },
      error: (error) => {
        console.error("Error generating schedule:", error);
        this.isGeneratingSchedule = false;
        this.alertService.displayAlert(
          "error",
          "Failed to generate schedule. Please try again later.",
          "center",
          "top",
          ["error-snackbar"]
        );
      },
    });
  }

  moveTopic(
    weekNumber: number,
    sessionId: string,
    topicId: string,
    topic: ICourseTopic
  ) {
    if (!this.project?.weeks) return;

    const dialogRef = this.dialog.open(MoveTopicDialogComponent, {
      width: "450px",
      data: {
        topic,
        currentWeekNumber: weekNumber,
        currentSessionId: sessionId,
        weeks: this.project.weeks,
        projectStartDate: this.project.startDate,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.alertService.displayAlert(
          "info",
          "Move topic to different day not implemented yet",
          "center",
          "top",
          ["info-snackbar"]
        );
      }
    });
  }

  moveTopicUp(weekIndex: number, sessionIndex: number, topicIndex: number) {
    this.alertService.displayAlert(
      "info",
      "Move topic up not implemented yet",
      "center",
      "top",
      ["info-snackbar"]
    );
  }

  moveTopicDown(weekIndex: number, sessionIndex: number, topicIndex: number) {
    this.alertService.displayAlert(
      "info",
      "Move topic down not implemented yet",
      "center",
      "top",
      ["info-snackbar"]
    );
  }

  toggleLock(topic: ICourseTopic) {
    topic.teacherLocked = !topic.teacherLocked;
  }

  private generateWeeks() {
    if (!this.project?.startDate || !this.project?.endDate) return;

    if (this.project.weeks && this.project.weeks.length > 0) {
      return;
    }

    const startDate = new Date(this.project.startDate);
    const endDate = new Date(this.project.endDate);
    const selectedDays = Object.entries(this.project.selectedDays)
      .filter(([_, value]) => value)
      .map(([key]) => key.toLowerCase());

    this.weeks = [];
    let currentDate = new Date(startDate);
    let weekNumber = 1;

    while (currentDate <= endDate) {
      const weekStart = new Date(currentDate);
      const weekEnd = new Date(currentDate);
      weekEnd.setDate(weekEnd.getDate() + 6);

      if (weekEnd > endDate) {
        weekEnd.setTime(endDate.getTime());
      }

      const days: IWeek["days"] = [];
      const weekDate = new Date(weekStart);

      while (weekDate <= weekEnd) {
        const dayName = weekDate
          .toLocaleDateString("en-US", { weekday: "long" })
          .toLowerCase();
        const isTeachingDay = selectedDays.includes(dayName);

        days.push({
          date: new Date(weekDate),
          isTeachingDay,
          topics: isTeachingDay ? [] : [],
        });

        weekDate.setDate(weekDate.getDate() + 1);
      }

      this.weeks.push({
        weekNumber,
        startDate: weekStart,
        endDate: weekEnd,
        days,
      });

      currentDate.setDate(currentDate.getDate() + 7);
      weekNumber++;
    }
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
    if (!this.project?.startDate) return null;

    const projectStartDate = new Date(this.project.startDate);

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
    if (daysDiff < 0) daysDiff += 7;
    daysToAdd += daysDiff;

    projectStartDate.setDate(projectStartDate.getDate() + daysToAdd);

    return projectStartDate;
  }

  getSortedClassSessions(week: ICourseWeek): IClassSession[] {
    if (!week.classSessions || week.classSessions.length === 0) {
      return [];
    }

    return [...week.classSessions].sort((a, b) => {
      const dateA = this.getSessionDate(week.weekNumber, a.dayOfWeek);
      const dateB = this.getSessionDate(week.weekNumber, b.dayOfWeek);

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

  getWeekRange(week: ICourseWeek): string {
    const startDate = new Date(this.project?.startDate || new Date());
    startDate.setDate(startDate.getDate() + (week.weekNumber - 1) * 7);

    const endDate = new Date(startDate);
    endDate.setDate(endDate.getDate() + 6);

    return `${startDate.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
    })} - ${endDate.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
    })}`;
  }

  getDayName(date: Date): string {
    return date.toLocaleDateString("en-US", { weekday: "short" });
  }

  getCurrentMonth(): string {
    return this.calendarStartDate.toLocaleDateString("en-US", {
      month: "long",
      year: "numeric",
    });
  }

  getCalendarDays() {
    if (!this.project) return [];

    const selectedDays = Object.entries(this.project.selectedDays)
      .filter(([_, value]) => value)
      .map(([key]) => key.toLowerCase());

    const startDate = new Date(this.project.startDate || "");
    const endDate = new Date(this.project.endDate || "");
    const days: {
      date: Date | null;
      isTeachingDay: boolean;
      isToday: boolean;
      isWeekStart: boolean;
      isWeekEnd: boolean;
    }[] = [];

    const year = this.calendarStartDate.getFullYear();
    const month = this.calendarStartDate.getMonth();
    const startOfMonth = new Date(year, month, 1);
    const endOfMonth = new Date(year, month + 1, 0);
    const weekdayIndex = (startOfMonth.getDay() + 6) % 7;

    for (let i = 0; i < weekdayIndex; i++) {
      days.push({
        date: null,
        isTeachingDay: false,
        isToday: false,
        isWeekStart: false,
        isWeekEnd: false,
      });
    }

    for (let d = 1; d <= endOfMonth.getDate(); d++) {
      const date = new Date(year, month, d);
      const dayName = date
        .toLocaleDateString("en-US", { weekday: "long" })
        .toLowerCase();
      const isAfterEndDate = date > endDate;
      const isBeforeStartDate = date < startDate;
      const isToday = date.toDateString() === new Date().toDateString();
      const isTeachingDay =
        !isAfterEndDate && !isBeforeStartDate && selectedDays.includes(dayName);
      const isWeekStart = d === 1 || date.getDay() === 1;
      const isWeekEnd = d === endOfMonth.getDate() || date.getDay() === 0;

      days.push({
        date,
        isTeachingDay,
        isToday,
        isWeekStart,
        isWeekEnd,
      });
    }

    return days;
  }

  goForward() {
    const endDate = new Date(this.project?.endDate || "");
    const currentMonth = this.calendarStartDate.getMonth();
    const currentYear = this.calendarStartDate.getFullYear();
    const nextDate = new Date(currentYear, currentMonth + 1, 1);

    if (
      nextDate.getFullYear() < endDate.getFullYear() ||
      (nextDate.getFullYear() === endDate.getFullYear() &&
        nextDate.getMonth() <= endDate.getMonth())
    ) {
      this.calendarStartDateChange.emit(nextDate);
    }
  }

  goBack() {
    const startDate = new Date(this.project?.startDate || "");
    const prevMonthDate = new Date(
      this.calendarStartDate.getFullYear(),
      this.calendarStartDate.getMonth() - 1,
      1
    );

    if (
      prevMonthDate.getFullYear() > startDate.getFullYear() ||
      (prevMonthDate.getFullYear() === startDate.getFullYear() &&
        prevMonthDate.getMonth() >= startDate.getMonth())
    ) {
      this.calendarStartDateChange.emit(prevMonthDate);
    }
  }

  resetToToday() {
    this.calendarStartDateChange.emit(new Date());
  }

  isNextMonthBeyondEndDate(): boolean {
    const endDate = new Date(this.project?.endDate || "");
    const nextMonthDate = new Date(
      this.calendarStartDate.getFullYear(),
      this.calendarStartDate.getMonth() + 1,
      1
    );

    return (
      nextMonthDate.getFullYear() > endDate.getFullYear() ||
      (nextMonthDate.getFullYear() === endDate.getFullYear() &&
        nextMonthDate.getMonth() > endDate.getMonth())
    );
  }

  isPrevMonthBeforeStartDate(): boolean {
    const startDate = new Date(this.project?.startDate || "");
    const prevMonthDate = new Date(
      this.calendarStartDate.getFullYear(),
      this.calendarStartDate.getMonth() - 1,
      1
    );

    return (
      prevMonthDate.getFullYear() < startDate.getFullYear() ||
      (prevMonthDate.getFullYear() === startDate.getFullYear() &&
        prevMonthDate.getMonth() < startDate.getMonth())
    );
  }
}
