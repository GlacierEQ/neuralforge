import { CommonModule } from "@angular/common";
import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  Output,
  TemplateRef,
} from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatTabsModule } from "@angular/material/tabs";
import {
  IProgrammedGoalProject,
  IProject,
  IProjectType,
} from "../../interfaces";

@Component({
  selector: "app-project-details",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatTabsModule,
    MatIconModule,
    MatSlideToggleModule,
  ],
  templateUrl: "./project-details.component.html",
  styleUrls: ["./project-details.component.scss"],
})
export class ProjectDetailsComponent {
  @Input() project: IProject | null = null;
  @Input() isLoading = false;
  @Input() hasError = false;
  @Input() errorMessage = "";

  @ContentChild("overviewContent") overviewContent!: TemplateRef<any>;
  @ContentChild("generatedContent") generatedContent!: TemplateRef<any>;
  @ContentChild("configurationContent") configurationContent!: TemplateRef<any>;

  @Output() notificationsToggle = new EventEmitter<boolean>();
  @Output() editProject = new EventEmitter<void>();
  @Output() deleteProject = new EventEmitter<void>();

  get isProgrammedGoalProject(): boolean {
    return this.project?.projectType === IProjectType.ProgrammedGoal;
  }

  get notificationsEnabled(): boolean {
    if (this.isProgrammedGoalProject) {
      return (this.project as IProgrammedGoalProject).notify;
    }
    return false;
  }

  onToggleNotify() {
    if (this.isProgrammedGoalProject) {
      this.notificationsToggle.emit(!this.notificationsEnabled);
    }
  }
}
