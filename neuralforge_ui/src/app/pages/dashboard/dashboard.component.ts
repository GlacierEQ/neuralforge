import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";

import { CreateProjectDialogComponent } from "../../components/dialogs/create-project-dialog/create-project-dialog.component";
import { CreateGoalProjectDialogComponent } from "../../components/dialogs/create-goal-project-dialog/create-goal-project-dialog.component";
import { CreateStudyPlanDialogComponent } from "../../components/dialogs/create-study-plan-dialog/create-study-plan-dialog.component";
import { EmptyStateComponent } from "../../components/empty-state/empty-state.component";

import { ILearningProject, IDashboardSection } from "../../interfaces";
import { LearningProjectService } from "../../services/learning-project.service";
import { ProgrammedGoalProjectService } from "../../services/programmed-goal-project.service";

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIcon,
    EmptyStateComponent,
    RouterLink,
  ],
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit {
  constructor(
      private dialog: MatDialog,
      private learningProjectService: LearningProjectService,
      private programmedGoalProjectService: ProgrammedGoalProjectService
  ) {}

  welcomeMessages = [
    {
      icon: "emoji_people",
      text: "Welcome back! Ready to explore your learning space?",
    },
    {
      icon: "tips_and_updates",
      text: "Tip: Check your Study Plans to get started quickly.",
    },
    { icon: "star", text: "You’re doing great! Keep up the progress!" },
  ];

  currentMessageIndex = 0;

  sections: IDashboardSection[] = [
    {
      title: "Study Plans",
      buttonText: "Create New Study Plan",
      buttonAction: this.openCreateStudyPlanDialog.bind(this),
      isLoading: false,
      cards: [],
    },
    {
      title: "Learning Projects",
      buttonText: "Create New Learning Project",
      buttonAction: this.openCreateLearningProjectDialog.bind(this),
      isLoading: true,
      cards: [],
    },
    {
      title: "Programmed Goal Projects",
      buttonText: "Create New Programmed Goal Project",
      buttonAction: this.openCreateGoalProjectDialog.bind(this),
      isLoading: true,
      cards: [],
    },
  ];

  ngOnInit() {
    setInterval(() => {
      this.currentMessageIndex =
          (this.currentMessageIndex + 1) % this.welcomeMessages.length;
    }, 5000); // Rotate welcome messages every 5 seconds

    this.fetchLearningProjects();
    this.fetchProgrammedGoalProjects();
  }

  openCreateLearningProjectDialog() {
    const dialogRef = this.dialog.open(CreateProjectDialogComponent);

    dialogRef.afterClosed().subscribe((result: ILearningProject) => {
      if (result) {
        const section = this.sections.find(
            (s) => s.title === "Learning Projects"
        );
        if (section) {
          section.cards.push({
            id: result.id,
            title: result.name,
            content: result.description || "No description available.",
            projectType: result.projectType,
          });
        }
      }
    });
  }

  openCreateGoalProjectDialog() {
    const dialogRef = this.dialog.open(CreateGoalProjectDialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const section = this.sections.find(
            (s) => s.title === "Programmed Goal Projects"
        );
        if (section) {
          section.cards.push({
            id: result.id,
            title: result.name,
            content: result.description || "No description available.",
            projectType: result.projectType,
          });
        }
      }
    });
  }

  openCreateStudyPlanDialog() {
    const dialogRef = this.dialog.open(CreateStudyPlanDialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const section = this.sections.find(
            (s) => s.title === "Study Plans"
        );
        if (section) {
          console.log("Study plan created:", result);
          // Future: push to section.cards if applicable
        }
      }
    });
  }

  fetchLearningProjects() {
    const section = this.sections.find(
        (s) => s.title === "Learning Projects"
    );
    if (!section) return;

    section.isLoading = true;
    section.hasError = false;
    section.errorMessage = "";

    this.learningProjectService
        .findAllWithParamsAndCustomSource("mine")
        .subscribe({
          next: (response) => {
            if (Array.isArray(response)) {
              section.cards = response.map((project: ILearningProject) => ({
                id: project.id,
                title: project.name,
                content: project.description || "No description available.",
                projectType: project.projectType,
              }));
            }
            section.isLoading = false;
          },
          error: (error) => {
            console.error("Error fetching learning projects:", error);
            section.cards = [];
            section.isLoading = false;
            section.hasError = true;
            section.errorMessage =
                "Unable to load projects. Please try again later.";
          },
        });
  }

  fetchProgrammedGoalProjects() {
    const section = this.sections.find(
        (s) => s.title === "Programmed Goal Projects"
    );
    if (!section) return;

    section.isLoading = true;
    section.hasError = false;
    section.errorMessage = "";

    this.programmedGoalProjectService.findMine().subscribe({
      next: (response) => {
        if (Array.isArray(response)) {
          section.cards = response.map((project: ILearningProject) => ({
            id: project.id,
            title: project.name,
            content: project.description || "No description available.",
            projectType: project.projectType,
          }));
        }
        section.isLoading = false;
      },
      error: (error) => {
        console.error("Error fetching programmed goal projects:", error);
        section.cards = [];
        section.isLoading = false;
        section.hasError = true;
        section.errorMessage =
            "Unable to load projects. Please try again later.";
      },
    });
  }
}
