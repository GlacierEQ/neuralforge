import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { FormsModule } from '@angular/forms';
import {ISelectedDays} from "../../interfaces";

@Component({
  selector: 'app-weekday-picker',
  standalone: true,
  imports: [CommonModule, MatButtonToggleModule, FormsModule],
  templateUrl: './weekday-picker.component.html',
  styleUrls: ['./weekday-picker.component.scss']
})
export class WeekdayPickerComponent {
  @Input() selectedDays: ISelectedDays = {
    monday: false,
    tuesday: false,
    wednesday: false,
    thursday: false,
    friday: false,
    saturday: false,
    sunday: false
  };
  @Output() selectedDaysChange = new EventEmitter<ISelectedDays>();

  weekdays = [
    { key: 'monday', label: 'Mon' },
    { key: 'tuesday', label: 'Tue' },
    { key: 'wednesday', label: 'Wed' },
    { key: 'thursday', label: 'Thu' },
    { key: 'friday', label: 'Fri' },
    { key: 'saturday', label: 'Sat' },
    { key: 'sunday', label: 'Sun' },
  ];

  toggleDay(key: string): void {
    this.selectedDays[key] = !this.selectedDays[key];
    this.selectedDaysChange.emit(this.selectedDays);
  }

}