import {
    Component,
    Input,
    Output,
    EventEmitter
} from '@angular/core';
import { CommonModule, DatePipe, NgClass, NgFor } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatCard} from "@angular/material/card";

@Component({
    selector: 'app-deadline-calendar',
    standalone: true,
    imports: [CommonModule, MatButtonModule, MatIconModule, DatePipe, NgClass, NgFor, MatCard],
    templateUrl: './deadline-calendar.component.html',
    styleUrls: ['./deadline-calendar.component.scss']
})
export class DeadlineCalendarComponent {
    @Input() calendarStartDate!: Date;
    @Input() today!: Date;
    @Input() project: any;

    @Output() calendarStartDateChange = new EventEmitter<Date>();

    getCurrentMonth(): string {
        return this.calendarStartDate.toLocaleDateString('en-US', {
            month: 'long',
            year: 'numeric'
        });
    }

    getCalendarDays() {
        if (!this.project) return [];

        const selectedDays = Object.entries(this.project.selectedDays)
            .filter(([_, value]) => value)
            .map(([key]) => key.toLowerCase());

        const deadline = new Date(this.project.deadline);
        const createdAt = new Date(this.project.createdAt);
        const days: {
            date: Date | null;
            isStudyDay: boolean;
            isDeadline: boolean;
            isToday: boolean;
            isCreatedAtStudyDay?: boolean;
        }[] = [];

        const year = this.calendarStartDate.getFullYear();
        const month = this.calendarStartDate.getMonth();
        const startOfMonth = new Date(year, month, 1);
        const endOfMonth = new Date(year, month + 1, 0);
        const weekdayIndex = (startOfMonth.getDay() + 6) % 7;

        for (let i = 0; i < weekdayIndex; i++) {
            days.push({ date: null, isStudyDay: false, isDeadline: false, isToday: false });
        }

        for (let d = 1; d <= endOfMonth.getDate(); d++) {
            const date = new Date(year, month, d);
            const dayName = date.toLocaleDateString('en-US', { weekday: 'long' }).toLowerCase();
            const isAfterDeadline = date > deadline;
            const isBeforeCreatedAt = date < new Date(createdAt.getFullYear(), createdAt.getMonth(), createdAt.getDate());
            const isToday = date.toDateString() === this.today.toDateString();
            const isCreatedAtDay = date.toDateString() === createdAt.toDateString();
            const isStudyDay = !isAfterDeadline && !isBeforeCreatedAt && selectedDays.includes(dayName);

            days.push({
                date,
                isStudyDay,
                isDeadline: date.toDateString() === deadline.toDateString(),
                isToday,
                isCreatedAtStudyDay: isCreatedAtDay && selectedDays.includes(dayName)
            });
        }

        return days;
    }


    goForward() {
        const deadline = new Date(this.project?.deadline || '');
        const currentMonth = this.calendarStartDate.getMonth();
        const currentYear = this.calendarStartDate.getFullYear();
        const nextDate = new Date(currentYear, currentMonth + 1, 1);

        if (
            nextDate.getFullYear() < deadline.getFullYear() ||
            (nextDate.getFullYear() === deadline.getFullYear() && nextDate.getMonth() <= deadline.getMonth())
        ) {
            this.calendarStartDateChange.emit(nextDate);
        }
    }

    goBack() {
        const createdAt = new Date(this.project.createdAt);
        const prevMonthDate = new Date(
            this.calendarStartDate.getFullYear(),
            this.calendarStartDate.getMonth() - 1,
            1
        );

        if (
            prevMonthDate.getFullYear() > createdAt.getFullYear() ||
            (prevMonthDate.getFullYear() === createdAt.getFullYear() &&
                prevMonthDate.getMonth() >= createdAt.getMonth())
        ) {
            this.calendarStartDateChange.emit(prevMonthDate);
        }
    }

    resetToToday() {
        this.calendarStartDateChange.emit(new Date(this.today.getFullYear(), this.today.getMonth(), 1));
    }

    isNextMonthBeyondDeadline(): boolean {
        const deadline = new Date(this.project.deadline);
        const nextMonthDate = new Date(this.calendarStartDate.getFullYear(), this.calendarStartDate.getMonth() + 1, 1);

        return (
            nextMonthDate.getFullYear() > deadline.getFullYear() ||
            (nextMonthDate.getFullYear() === deadline.getFullYear() &&
                nextMonthDate.getMonth() > deadline.getMonth())
        );
    }

    isPrevMonthBeforeCreatedAt(): boolean {
        const createdAt = new Date(this.project.createdAt);
        const prevMonthDate = new Date(this.calendarStartDate.getFullYear(), this.calendarStartDate.getMonth() - 1, 1);

        return (
            prevMonthDate.getFullYear() < createdAt.getFullYear() ||
            (prevMonthDate.getFullYear() === createdAt.getFullYear() &&
                prevMonthDate.getMonth() < createdAt.getMonth())
        );
    }
}
