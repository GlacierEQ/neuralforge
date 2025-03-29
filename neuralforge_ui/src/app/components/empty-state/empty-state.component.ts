import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: "app-empty-state",
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: "./empty-state.component.html",
  styleUrls: ["./empty-state.component.scss"],
})
export class EmptyStateComponent {
  @Input() icon: string = "folder_open";
  @Input() title: string = "No items found";
  @Input() message: string = "There are no items to display right now.";
  @Input() buttonText: string = "";
  @Output() onButtonClick = new EventEmitter<void>();
}
