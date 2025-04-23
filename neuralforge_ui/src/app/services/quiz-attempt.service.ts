import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { QuizAttempt, QuizUserAnswer } from "../models/quiz.model";

@Injectable({
  providedIn: "root",
})
export class QuizAttemptService {
  private baseUrl = "api/neuralforge/v1/quizzes";

  constructor(private http: HttpClient) {}

  startQuizAttempt(quizId: string): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(
      `${this.baseUrl}/${quizId}/attempts`,
      null
    );
  }

  submitAnswer(
    attemptId: string,
    userAnswer: QuizUserAnswer
  ): Observable<QuizUserAnswer> {
    return this.http.post<QuizUserAnswer>(
      `${this.baseUrl}/attempts/${attemptId}/answers`,
      userAnswer
    );
  }

  completeAttempt(attemptId: string): Observable<QuizAttempt> {
    return this.http
      .post<QuizAttempt>(`${this.baseUrl}/attempts/${attemptId}/complete`, null)
      .pipe(map((response: any) => this.mapServerResponse(response)));
  }

  getQuizAttempts(quizId: string): Observable<QuizAttempt[]> {
    return this.http.get<QuizAttempt[]>(`${this.baseUrl}/${quizId}/attempts`);
  }

  getQuizAttempt(attemptId: string): Observable<QuizAttempt> {
    return this.http
      .get<QuizAttempt>(`${this.baseUrl}/attempts/${attemptId}`)
      .pipe(map((response: any) => this.mapServerResponse(response)));
  }

  private mapServerResponse(response: any): any {
    if (!response) return response;

    if (response.userAnswers && Array.isArray(response.userAnswers)) {
      response.userAnswers = response.userAnswers.map((answer: any) => {
        if (answer.correct !== undefined && answer.isCorrect === undefined) {
          return { ...answer, isCorrect: answer.correct };
        }
        return answer;
      });
    }

    return response;
  }
}
