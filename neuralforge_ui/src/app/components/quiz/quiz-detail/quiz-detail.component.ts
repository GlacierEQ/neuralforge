import { CommonModule, Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog } from "@angular/material/dialog";
import { MatDividerModule } from "@angular/material/divider";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTooltipModule } from "@angular/material/tooltip";
import { ActivatedRoute, Router } from "@angular/router";
import { Quiz, QuizAttempt } from "../../../models/quiz.model";
import { ConfirmDialogComponent } from "../../../components/dialogs/confirm-dialog/confirm-dialog.component";
import { AlertService } from "../../../services/alert.service";
import { QuizAttemptService } from "../../../services/quiz-attempt.service";
import { QuizService } from "../../../services/quiz.service";

@Component({
  selector: "app-quiz-detail",
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatTooltipModule,
    MatDividerModule,
  ],
  templateUrl: "./quiz-detail.component.html",
  styleUrls: ["./quiz-detail.component.scss"],
})
export class QuizDetailComponent implements OnInit {
  quizId!: string;
  projectId!: string;
  quiz: Quiz | null = null;
  isLoading = true;
  error: string | null = null;

  quizAttempts: QuizAttempt[] = [];
  loadingAttempts = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private alertService: AlertService,
    private _location: Location,
    private quizAttemptService: QuizAttemptService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get("quizId");
      const projectId = params.get("id");

      if (id && projectId) {
        this.quizId = id;
        this.projectId = projectId;
        this.loadQuiz();
      } else {
        this.router.navigate(["/projects"]);
      }
    });
  }

  loadQuiz(): void {
    this.isLoading = true;
    this.error = null;

    this.quizService.getQuiz(this.quizId).subscribe({
      next: (quiz) => {
        this.quiz = quiz;
        this.isLoading = false;
        this.loadQuizAttempts();
      },
      error: (err) => {
        this.error = "Failed to load quiz. Please try again.";
        this.isLoading = false;
        console.error("Error loading quiz:", err);
      },
    });
  }

  loadQuizAttempts(): void {
    this.loadingAttempts = true;

    this.quizAttemptService.getQuizAttempts(this.quizId).subscribe({
      next: (attempts) => {
        this.quizAttempts = attempts;
        this.loadingAttempts = false;
      },
      error: (err) => {
        console.error("Error loading quiz attempts:", err);
        this.loadingAttempts = false;
      },
    });
  }

  deleteQuiz(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Quiz',
        message: `Are you sure you want to delete the quiz "${this.quiz?.title}"?`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      },
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.quizService.deleteQuiz(this.quizId).subscribe({
          next: () => {
            this.alertService.displayAlert(
              "success",
              "Quiz deleted successfully"
            );
            this.navigateBack();
          },
          error: (err) => {
            this.alertService.displayAlert(
              "error",
              "Failed to delete quiz. Please try again."
            );
            console.error("Error deleting quiz:", err);
          },
        });
      }
    });
  }

  navigateBack(): void {
    switch (this.quiz?.projectType) {
      case "LEARNING":
        this.router.navigate(["app/project/learning", this.projectId]);
        break;
      case "TEACHING":
        this.router.navigate(["app/project/teaching", this.projectId]);
        break;
      case "PROGRAMMED_GOAL":
        this.router.navigate(["app/project/programmed_goal", this.projectId]);
        break;
    }
  }

  formatDate(date: Date | undefined): string {
    return date ? new Date(date).toLocaleDateString() : "";
  }

  startQuiz(): void {
    this.router.navigate(["app/quizzes", this.quizId, "take"]);
  }

  viewAttemptDetails(attemptId: string): void {
    this.router.navigate(["app/quizzes", this.quizId, "attempts", attemptId]);
  }
}
