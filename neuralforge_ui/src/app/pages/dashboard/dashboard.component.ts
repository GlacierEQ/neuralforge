import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";

import { CreateGoalProjectDialogComponent } from "../../components/dialogs/create-goal-project-dialog/create-goal-project-dialog.component";
import { CreateProjectDialogComponent } from "../../components/dialogs/create-project-dialog/create-project-dialog.component";
import { CreateTeachingProjectDialogComponent } from "../../components/dialogs/create-teaching-project-dialog/create-teaching-project-dialog.component";
import { EmptyStateComponent } from "../../components/empty-state/empty-state.component";

import {
  IDashboardSection,
  ILearningProject,
  IProgrammedGoalProject,
  ITeachingProject,
} from "../../interfaces";
import { LearningProjectService } from "../../services/learning-project.service";
import { ProgrammedGoalProjectService } from "../../services/programmed-goal-project.service";
import { TeachingProjectService } from "../../services/teaching-project.service";

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
    private programmedGoalProjectService: ProgrammedGoalProjectService,
    private teachingProjectService: TeachingProjectService
  ) {}

  welcomeMessages = [
    {
      icon: "emoji_people",
      text: "Welcome back! Ready to explore your learning space?",
    },
    {
      icon: "tips_and_updates",
      text: "Tip: Check your Teaching Projects to get started quickly.",
    },
    { icon: "star", text: "You're doing great! Keep up the progress!" },
  ];

  currentMessageIndex = 0;

  sections: IDashboardSection[] = [
    {
      title: "Teaching Projects",
      buttonText: "Create New Teaching Project",
      buttonAction: this.openCreateTeachingProjectDialog.bind(this),
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
    }, 5000);

    this.fetchTeachingProjects();
    this.fetchLearningProjects();
    this.fetchProgrammedGoalProjects();
  }

  openCreateTeachingProjectDialog() {
    const dialogRef = this.dialog.open(CreateTeachingProjectDialogComponent);

    dialogRef.afterClosed().subscribe((result: ITeachingProject) => {
      if (result) {
        const section = this.sections.find(
          (s) => s.title === "Teaching Projects"
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

  fetchTeachingProjects() {
    const section = this.sections.find((s) => s.title === "Teaching Projects");
    if (!section) return;

    section.isLoading = true;
    section.hasError = false;
    section.errorMessage = "";

    this.teachingProjectService
      .findAllWithParamsAndCustomSource("mine")
      .subscribe({
        next: (response) => {
          console.log("Teaching projects response:", response);
          if (Array.isArray(response)) {
            section.cards = response.map((project: ITeachingProject) => ({
              id: project.id,
              title: project.name,
              content: project.description || "No description available.",
              projectType: project.projectType,
            }));
            console.log("Teaching projects cards:", section.cards);
          } else {
            console.log("Response is not an array:", response);
          }
          section.isLoading = false;
        },
        error: (error) => {
          console.error("Error fetching teaching projects:", error);
          section.cards = [];
          section.isLoading = false;
          section.hasError = true;
          section.errorMessage =
            "Unable to load projects. Please try again later.";
        },
      });
  }

  fetchLearningProjects() {
    const section = this.sections.find((s) => s.title === "Learning Projects");
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
          section.cards = response.map((project: IProgrammedGoalProject) => ({
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
