import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatListModule } from "@angular/material/list";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";

import { DatePipe } from "@angular/common";
import { QuizAttempt, QuizUserAnswer } from "../../../models/quiz.model";
import { FilterCorrectPipe } from "../../../pipes/filter-correct.pipe";
import { AlertService } from "../../../services/alert.service";
import { QuizAttemptService } from "../../../services/quiz-attempt.service";

@Component({
  selector: "app-quiz-result",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatListModule,
    MatProgressBarModule,
    DatePipe,
    FilterCorrectPipe,
  ],
  templateUrl: "./quiz-result.component.html",
  styleUrls: ["./quiz-result.component.scss"],
})
export class QuizResultComponent implements OnInit {
  quizId: string = "";
  attemptId: string = "";
  attempt: QuizAttempt | null = null;
  loading: boolean = true;
  error: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private attemptService: QuizAttemptService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get("quizId") || "";
    this.attemptId = this.route.snapshot.paramMap.get("attemptId") || "";

    if (!this.quizId || !this.attemptId) {
      this.error = true;
      this.loading = false;
      this.alertService.displayAlert("error", "Invalid quiz or attempt ID");
      return;
    }

    this.loadQuizAttempt();
  }

  loadQuizAttempt(): void {
    this.attemptService.getQuizAttempt(this.attemptId).subscribe({
      next: (attempt) => {
        this.attempt = attempt;
        this.loading = false;
      },
      error: (err) => {
        this.error = true;
        this.loading = false;
        this.alertService.displayAlert(
          "error",
          "Failed to load quiz results. Please try again."
        );
        console.error("Error loading quiz attempt:", err);
      },
    });
  }

  getScoreColor(): string {
    if (!this.attempt) return "";

    if (this.attempt.score >= 80) {
      return "score-excellent";
    } else if (this.attempt.score >= 60) {
      return "score-good";
    } else {
      return "score-needs-improvement";
    }
  }

  getScoreMessage(): string {
    if (!this.attempt) return "";

    if (this.attempt.score >= 80) {
      return "Excellent! You have a strong understanding of the material.";
    } else if (this.attempt.score >= 60) {
      return "Good job! You understand most of the material, but there's room for improvement.";
    } else {
      return "Keep practicing! Review the material and try again to improve your score.";
    }
  }

  retakeQuiz(): void {
    this.router.navigate(["app/quizzes", this.quizId, "take"]);
  }

  getCorrectAnswer(userAnswer: QuizUserAnswer): string {
    if (!this.attempt || !this.attempt.userAnswers) {
      return "Unknown";
    }

    if (userAnswer.correctAnswerText) {
      return userAnswer.correctAnswerText;
    }

    const otherAnswers = this.attempt.userAnswers.filter(
      (a) => a.questionId === userAnswer.questionId && a.isCorrect
    );

    if (otherAnswers.length > 0) {
      return otherAnswers[0].selectedAnswerText;
    }

    return "Unknown";
  }

  navigateToDashboard(): void {
    this.router.navigate(["app/dashboard"]);
  }
}
