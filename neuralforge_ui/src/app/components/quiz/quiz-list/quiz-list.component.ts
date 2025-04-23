import { CommonModule } from "@angular/common";
import { Component, Input, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTooltipModule } from "@angular/material/tooltip";
import { Router } from "@angular/router";
import { Quiz } from "../../../models/quiz.model";
import { AlertService } from "../../../services/alert.service";
import { QuizService } from "../../../services/quiz.service";
import { QuizGenerateDialogComponent } from "../quiz-generate-dialog/quiz-generate-dialog.component";

@Component({
  selector: "app-quiz-list",
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: "./quiz-list.component.html",
  styleUrls: ["./quiz-list.component.scss"],
})
export class QuizListComponent implements OnInit {
  @Input() projectId!: string;

  quizzes: Quiz[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private quizService: QuizService,
    private alertService: AlertService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.isLoading = true;
    this.error = null;

    this.quizService.getQuizzesByProject(this.projectId).subscribe({
      next: (quizzes) => {
        this.quizzes = quizzes;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = "Failed to load quizzes. Please try again.";
        this.isLoading = false;
        console.error("Error loading quizzes:", err);
      },
    });
  }

  openGenerateQuizDialog(): void {
    const dialogRef = this.dialog.open(QuizGenerateDialogComponent, {
      width: "500px",
      data: { projectId: this.projectId },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadQuizzes();
      }
    });
  }

  deleteQuiz(quiz: Quiz, event: Event): void {
    event.stopPropagation(); // Prevent card click event

    if (confirm(`Are you sure you want to delete the quiz "${quiz.title}"?`)) {
      this.quizService.deleteQuiz(quiz.id).subscribe({
        next: () => {
          this.alertService.displayAlert(
            "success",
            "Quiz deleted successfully"
          );
          this.loadQuizzes();
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
  }

  navigateToQuiz(quiz: Quiz): void {
    this.router.navigate(["app/projects", this.projectId, "quiz", quiz.id]);
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString();
  }
}
