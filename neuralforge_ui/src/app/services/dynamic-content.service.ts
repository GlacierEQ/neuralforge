import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { IDynamicContent } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class DynamicContentService extends BaseService<IDynamicContent> {
  protected override source = "api/neuralforge/v1/DynamicContent";

  getByProjectId(projectId: string): Observable<IDynamicContent[]> {
    return this.http.get<IDynamicContent[]>(
      `${this.source}/project/${projectId}`
    );
  }

  generateContent(
    projectId: string,
    materialId: string,
    title: string,
    type: string
  ): Observable<void> {
    return this.http.post<void>(`${this.source}/generate`, {
      projectId,
      materialId,
      title,
      type,
    });
  }

  download(contentId: string): Observable<ArrayBuffer> {
    return this.http.get(`${this.source}/download/${contentId}`, {
      responseType: "arraybuffer",
    });
  }
}
