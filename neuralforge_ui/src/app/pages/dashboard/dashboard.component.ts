import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { CreateProjectDialogComponent } from "../../components/dialogs/create-project-dialog/create-project-dialog.component";
import { EmptyStateComponent } from "../../components/empty-state/empty-state.component";
import { ILearningProject } from "../../interfaces";
import { LearningProjectService } from "../../services/learning-project.service";

interface DashboardCard {
  title: string;
  content: string;
  count?: number;
  id?: string | number;
}

interface DashboardSection {
  title: string;
  buttonText: string;
  buttonAction?: () => void;
  cards: DashboardCard[];
  isLoading?: boolean;
  hasError?: boolean;
  errorMessage?: string;
}

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIcon,
    EmptyStateComponent,
  ],
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit {
  private dialog: MatDialog;
  private learningProjectService: LearningProjectService;

  constructor(
    dialog: MatDialog,
    learningProjectService: LearningProjectService
  ) {
    this.dialog = dialog;
    this.learningProjectService = learningProjectService;
  }

  openCreateLearningProjectDialog() {
    const dialogRef = this.dialog.open(CreateProjectDialogComponent);

    dialogRef.afterClosed().subscribe((result: ILearningProject) => {
      if (result) {
        const learningProjectSection = this.sections.find(
          (section) => section.title === "Learning Projects"
        );

        if (learningProjectSection) {
          learningProjectSection.cards.push({
            id: result.id,
            title: result.name,
            content: result.description || "No description available.",
          });
        }
      }
    });
  }

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

  sections: DashboardSection[] = [
    {
      title: "Study Plans",
      buttonText: "Create New Study Plan",
      isLoading: false,
      cards: [
        {
          title: "Study Plan 1",
          content: "Lorem ipsum dolor sit amet...",
          count: 30,
        },
        {
          title: "Study Plan 2",
          content: "Lorem ipsum dolor sit amet...",
          count: 10,
        },
        {
          title: "Study Plan 3",
          content: "Lorem ipsum dolor sit amet...",
          count: 20,
        },
      ],
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
      isLoading: false,
      cards: [
        {
          title: "Goal Project 1",
          content: "Lorem ipsum dolor sit amet...",
          count: 30,
        },
        {
          title: "Goal Project 2",
          content: "Lorem ipsum dolor sit amet...",
          count: 60,
        },
        {
          title: "Goal Project 3",
          content: "Lorem ipsum dolor sit amet...",
          count: 20,
        },
      ],
    },
  ];

  ngOnInit() {
    setInterval(() => {
      this.currentMessageIndex =
        (this.currentMessageIndex + 1) % this.welcomeMessages.length;
    }, 5000); // change message every 5 seconds

    this.fetchLearningProjects();
  }

  fetchLearningProjects() {
    const learningProjectSection = this.sections.find(
      (section) => section.title === "Learning Projects"
    );
    if (!learningProjectSection) return;

    // Reset section state
    learningProjectSection.isLoading = true;
    learningProjectSection.hasError = false;
    learningProjectSection.errorMessage = "";

    // Call the API to get user's learning projects
    this.learningProjectService
      .findAllWithParamsAndCustomSource("mine")
      .subscribe({
        next: (response) => {
          console.log({ response });
          // Convert the learning projects to dashboard cards
          if (response && Array.isArray(response)) {
            learningProjectSection.cards = response.map(
              (project: ILearningProject) => ({
                id: project.id,
                title: project.name,
                content: project.description || "No description available.",
              })
            );
          }

          learningProjectSection.isLoading = false;
        },
        error: (error) => {
          console.error("Error fetching learning projects:", error);
          learningProjectSection.cards = [];
          learningProjectSection.isLoading = false;
          learningProjectSection.hasError = true;
          learningProjectSection.errorMessage =
            "Unable to load projects. Please try again later.";
        },
      });
  }
}
