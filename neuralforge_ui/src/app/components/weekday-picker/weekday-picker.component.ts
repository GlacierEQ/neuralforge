import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-weekday-picker',
  standalone: true,
  imports: [CommonModule, MatButtonToggleModule, FormsModule],
  templateUrl: './weekday-picker.component.html',
  styleUrls: ['./weekday-picker.component.scss']
})
export class WeekdayPickerComponent {
  @Input() selectedDays: boolean[] = [false, false, false, false, false, false, false];
  @Output() selectedDaysChange = new EventEmitter<boolean[]>();

  weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  toggleDay(index: number): void {
    this.selectedDays[index] = !this.selectedDays[index];
    this.selectedDaysChange.emit(this.selectedDays);
  }
}