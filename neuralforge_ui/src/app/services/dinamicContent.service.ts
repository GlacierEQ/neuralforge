import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class DynamicContentService {
  private http = inject(HttpClient);
  private baseUrl = "api/neuralforge/v1/auth/DynamicContent";

  constructor() {}

  public generateSummary(
    file: File,
    title: string,
    email: string,
    type: string,
    projectId: string
  ): Observable<string> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("title", title);
    formData.append("email", email);
    formData.append("type", type);
    formData.append("projectId", projectId);
  
    return this.http.post<string>(`${this.baseUrl}/generateSummary`, formData, { responseType: 'text' as 'json' });
  }
}