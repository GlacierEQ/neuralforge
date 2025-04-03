import { Injectable } from "@angular/core";
import { Observable, map } from "rxjs";
import { IResponse, ITeachingProject } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class TeachingProjectService extends BaseService<ITeachingProject> {
  protected override source = "api/neuralforge/v1/teaching-projects";

  getById(id: string): Observable<ITeachingProject> {
    return this.http.get<ITeachingProject>(`${this.source}/${id}`);
  }

  update(project: ITeachingProject): Observable<ITeachingProject> {
    return this.http.put<ITeachingProject>(
      `${this.source}/${project.id}`,
      project
    );
  }

  public findMine() {
    return this.findAllWithParamsAndCustomSource("mine").pipe(
      map((response: IResponse<ITeachingProject[]>) => response.data)
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.source}/${id}`);
  }
}
