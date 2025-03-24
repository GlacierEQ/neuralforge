import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { CreateProjectDialogComponent } from "../../components/dialogs/create-project-dialog/create-project-dialog.component";

interface DashboardCard {
  title: string;
  content: string;
  count?: number;
}

interface DashboardSection {
  title: string;
  buttonText: string;
  cards: DashboardCard[];
}

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIcon],
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent {
  private dialog: MatDialog;

  constructor(dialog: MatDialog) {
    this.dialog = dialog;
  }

  openCreateLearningProjectDialog() {
    this.dialog.open(CreateProjectDialogComponent);
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

  ngOnInit() {
    setInterval(() => {
      this.currentMessageIndex =
        (this.currentMessageIndex + 1) % this.welcomeMessages.length;
    }, 5000); // change message every 5 seconds
  }

  sections: DashboardSection[] = [
    {
      title: "Study Plans",
      buttonText: "Create New Study Plan",
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
      cards: [
        {
          title: "Learning Project 1",
          content: "Lorem ipsum dolor sit amet...",
        },
        {
          title: "Learning Project 2",
          content: "Lorem ipsum dolor sit amet...",
        },
        {
          title: "Learning Project 3",
          content: "Lorem ipsum dolor sit amet...",
        },
      ],
    },
    {
      title: "Programmed Goal Projects",
      buttonText: "Create New Programmed Goal Project",
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
}
