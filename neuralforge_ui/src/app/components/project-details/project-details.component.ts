import { CommonModule } from "@angular/common";
import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
} from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatTabsModule } from "@angular/material/tabs";
import { Project } from "../../models/project.model";
import { QuizListComponent } from "../quiz/quiz-list/quiz-list.component";
import { GeneratedContentsComponent } from "./generated-contents/generated-contents.component";
import { MaterialsComponent } from "./materials/materials.component";

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
    MaterialsComponent,
    GeneratedContentsComponent,
    QuizListComponent,
  ],
  templateUrl: "./project-details.component.html",
  styleUrls: ["./project-details.component.scss"],
})
export class ProjectDetailsComponent implements OnInit {
  @Input() project: Project | null = null;
  @Input() isLoading = false;
  @Input() hasError = false;
  @Input() errorMessage = "";
  @Input() notificationsEnabled = false;

  @ContentChild("overviewContent") overviewContent!: TemplateRef<any>;
  @ContentChild("generatedContent") generatedContent!: TemplateRef<any>;
  @ContentChild("configurationContent") configurationContent!: TemplateRef<any>;

  @Output() editProject = new EventEmitter<void>();
  @Output() deleteProject = new EventEmitter<void>();
  @Output() toggleNotify = new EventEmitter<boolean>();
  @Output() notificationsToggle = new EventEmitter<void>();

  isProgrammedGoalProject = false;

  ngOnInit(): void {
    if (this.project) {
      this.isProgrammedGoalProject =
        this.project.projectType === "PROGRAMMED_GOAL";
    }
  }

  onToggleNotify(): void {
    this.toggleNotify.emit(this.notificationsEnabled);
    this.notificationsToggle.emit();
  }
}
