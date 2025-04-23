import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { Quiz } from "../models/quiz.model";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class QuizService extends BaseService<Quiz> {
  protected override source = "api/neuralforge/v1/quizzes";

  getQuiz(id: string): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.source}/${id}`);
  }

  getQuizzesByProject(projectId: string): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.source}/project/${projectId}`);
  }

  createQuiz(quiz: Quiz): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.source}`, quiz);
  }

  generateQuiz(
    projectId: string,
    title: string,
    description: string,
    questionCount: number
  ): Observable<Quiz> {
    return this.http.post<Quiz>(`${this.source}/generate`, null, {
      params: {
        projectId,
        title,
        description,
        questionCount: questionCount.toString(),
      },
    });
  }

  deleteQuiz(id: string): Observable<void> {
    return this.http.delete<void>(`${this.source}/${id}`);
  }
}
