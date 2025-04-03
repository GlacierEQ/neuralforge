import { Injectable } from "@angular/core";
import { Observable, map } from "rxjs";
import { ILearningProject, IResponse } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class LearningProjectService extends BaseService<ILearningProject> {
  protected override source = "api/neuralforge/v1/learning-projects";

  getById(id: string): Observable<ILearningProject> {
    return this.http.get<ILearningProject>(`${this.source}/${id}`);
  }

  update(project: ILearningProject): Observable<ILearningProject> {
    return this.http.put<ILearningProject>(
      `${this.source}/${project.id}`,
      project
    );
  }

  findMine(): Observable<ILearningProject[]> {
    return this.findAllWithParamsAndCustomSource("mine").pipe(
      map((response: IResponse<ILearningProject[]>) => response.data)
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.source}/${id}`);
  }
}
