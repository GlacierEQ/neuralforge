import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatRadioModule } from "@angular/material/radio";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";

import {
  Quiz,
  QuizAttempt,
  QuizQuestion,
  QuizUserAnswer,
} from "../../../models/quiz.model";
import { AlertService } from "../../../services/alert.service";
import { QuizAttemptService } from "../../../services/quiz-attempt.service";
import { QuizService } from "../../../services/quiz.service";

@Component({
  selector: "app-quiz-take",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatRadioModule,
    MatProgressBarModule,
    FormsModule,
  ],
  templateUrl: "./quiz-take.component.html",
  styleUrls: ["./quiz-take.component.scss"],
})
export class QuizTakeComponent implements OnInit {
  quizId: string = "";
  quiz: Quiz | null = null;
  attempt: QuizAttempt | null = null;
  loading: boolean = true;
  error: boolean = false;

  currentQuestionIndex: number = 0;
  selectedAnswerId: string | null = null;
  answerSubmitted: boolean = false;
  lastSubmittedAnswer: QuizUserAnswer | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    private attemptService: QuizAttemptService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get("quizId") || "";
    if (!this.quizId) {
      this.error = true;
      this.loading = false;
      this.alertService.displayAlert("error", "Invalid quiz ID");
      return;
    }

    this.loadQuiz();
  }

  loadQuiz(): void {
    this.quizService.getQuiz(this.quizId).subscribe({
      next: (quiz) => {
        this.quiz = quiz;
        this.startQuizAttempt();
      },
      error: (err) => {
        this.error = true;
        this.loading = false;
        this.alertService.displayAlert(
          "error",
          "Failed to load quiz. Please try again."
        );
        console.error("Error loading quiz:", err);
      },
    });
  }

  startQuizAttempt(): void {
    this.attemptService.startQuizAttempt(this.quizId).subscribe({
      next: (attempt) => {
        this.attempt = attempt;
        this.loading = false;
      },
      error: (err) => {
        this.error = true;
        this.loading = false;
        this.alertService.displayAlert(
          "error",
          "Failed to start quiz attempt. Please try again."
        );
        console.error("Error starting quiz attempt:", err);
      },
    });
  }

  get currentQuestion(): QuizQuestion | null {
    if (
      !this.quiz ||
      !this.quiz.questions ||
      this.quiz.questions.length === 0
    ) {
      return null;
    }

    if (this.currentQuestionIndex >= this.quiz.questions.length) {
      return null;
    }

    return this.quiz.questions[this.currentQuestionIndex];
  }

  get progress(): number {
    if (
      !this.quiz ||
      !this.quiz.questions ||
      this.quiz.questions.length === 0
    ) {
      return 0;
    }

    return (this.currentQuestionIndex / this.quiz.questions.length) * 100;
  }

  submitAnswer(): void {
    if (!this.currentQuestion || !this.selectedAnswerId || !this.attempt) {
      this.alertService.displayAlert("error", "Please select an answer");
      return;
    }

    const correctAnswer = this.currentQuestion.answers.find((a) => a.isCorrect);

    const userAnswer: QuizUserAnswer = {
      id: "",
      attemptId: this.attempt.id,
      questionId: this.currentQuestion.id,
      questionText: this.currentQuestion.questionText,
      selectedAnswerId: this.selectedAnswerId,
      selectedAnswerText: this.getAnswerText(this.selectedAnswerId),
      isCorrect: false,
      explanation: "",
      correctAnswerId: correctAnswer?.id,
      correctAnswerText: correctAnswer?.answerText,
    };

    this.attemptService.submitAnswer(this.attempt.id, userAnswer).subscribe({
      next: (response: any) => {
        const answer: QuizUserAnswer = {
          ...response,
          isCorrect:
            response.correct !== undefined
              ? response.correct
              : response.isCorrect,
          correctAnswerId:
            response.correctAnswerId || userAnswer.correctAnswerId,
          correctAnswerText:
            response.correctAnswerText || userAnswer.correctAnswerText,
        };
        this.lastSubmittedAnswer = answer;
        this.answerSubmitted = true;
      },
      error: (err) => {
        this.alertService.displayAlert(
          "error",
          "Failed to submit answer. Please try again."
        );
        console.error("Error submitting answer:", err);
      },
    });
  }

  getAnswerText(answerId: string): string {
    if (!this.currentQuestion) return "";
    const answer = this.currentQuestion.answers.find((a) => a.id === answerId);
    return answer ? answer.answerText : "";
  }

  nextQuestion(): void {
    if (!this.quiz || !this.quiz.questions) {
      return;
    }

    this.selectedAnswerId = null;
    this.answerSubmitted = false;
    this.lastSubmittedAnswer = null;

    if (this.currentQuestionIndex < this.quiz.questions.length - 1) {
      this.currentQuestionIndex++;
    } else {
      this.completeQuiz();
    }
  }

  completeQuiz(): void {
    if (!this.attempt) {
      return;
    }

    this.attemptService.completeAttempt(this.attempt.id).subscribe({
      next: (completedAttempt) => {
        this.attempt = completedAttempt;
        this.router.navigate([
          "app/quizzes",
          this.quizId,
          "attempts",
          this.attempt.id,
        ]);
      },
      error: (err) => {
        this.alertService.displayAlert(
          "error",
          "Failed to complete quiz. Please try again."
        );
        console.error("Error completing quiz:", err);
      },
    });
  }

  navigateBack(): void {
    window.history.back();
  }

  isAnswerCorrect(answerId: string): boolean {
    if (!this.answerSubmitted || !this.lastSubmittedAnswer) {
      return false;
    }

    // If the user selected this answer and it's correct
    if (
      this.lastSubmittedAnswer.selectedAnswerId === answerId &&
      this.lastSubmittedAnswer.isCorrect
    ) {
      return true;
    }

    // Or if this is the correct answer but user selected something else
    // This shows the correct answer when user selects wrong
    if (
      this.lastSubmittedAnswer.selectedAnswerId !== answerId &&
      !this.lastSubmittedAnswer.isCorrect
    ) {
      // First check if we have the correctAnswerId from the server response
      if (this.lastSubmittedAnswer.correctAnswerId) {
        return this.lastSubmittedAnswer.correctAnswerId === answerId;
      }

      // Fallback to checking answers in the current question if server didn't provide correctAnswerId
      const correctAnswer = this.currentQuestion?.answers.find(
        (a) => a.isCorrect
      );
      return correctAnswer?.id === answerId;
    }

    return false;
  }

  isAnswerIncorrect(answerId: string): boolean {
    if (!this.answerSubmitted || !this.lastSubmittedAnswer) {
      return false;
    }

    // Only mark the selected answer as incorrect when it's wrong
    return (
      this.lastSubmittedAnswer.selectedAnswerId === answerId &&
      !this.lastSubmittedAnswer.isCorrect
    );
  }
}
