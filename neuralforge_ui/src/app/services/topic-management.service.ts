import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ICourseTopic, ITeachingProject } from "../interfaces";

@Injectable({
  providedIn: "root",
})
export class TopicManagementService {
  private apiUrl = "api/neuralforge/v1/topic-management";

  constructor(private http: HttpClient) {}

  toggleTopicLock(topicId: string): Observable<ICourseTopic> {
    return this.http.put<ICourseTopic>(
      `${this.apiUrl}/topics/${topicId}/toggle-lock`,
      {}
    );
  }

  moveTopicToSession(
    topicId: string,
    targetWeekNumber: number,
    targetSessionId: string
  ): Observable<ITeachingProject> {
    return this.http.put<ITeachingProject>(
      `${this.apiUrl}/topics/${topicId}/move-to-session`,
      {},
      {
        params: {
          targetWeekNumber: targetWeekNumber.toString(),
          targetSessionId,
        },
      }
    );
  }

  moveTopicUp(topicId: string): Observable<ITeachingProject> {
    return this.http.put<ITeachingProject>(
      `${this.apiUrl}/topics/${topicId}/move-up`,
      {}
    );
  }

  moveTopicDown(topicId: string): Observable<ITeachingProject> {
    return this.http.put<ITeachingProject>(
      `${this.apiUrl}/topics/${topicId}/move-down`,
      {}
    );
  }
}
