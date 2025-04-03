import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { IProgrammedGoalProject, IResponse } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class ProgrammedGoalProjectService extends BaseService<IProgrammedGoalProject> {
  protected override source = "api/neuralforge/v1/programmed-goal-projects";
  public getById(id: string): Observable<IProgrammedGoalProject> {
    return this.http.get<IProgrammedGoalProject>(`${this.source}/${id}`);
  }

  public delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.source}/${id}`);
  }

  public update(
    project: IProgrammedGoalProject
  ): Observable<IProgrammedGoalProject> {
    return this.http.put<IProgrammedGoalProject>(
      `${this.source}/${project.id}`,
      project
    );
  }

  public findMine() {
    return this.findAllWithParamsAndCustomSource("mine").pipe(
      map((response: IResponse<IProgrammedGoalProject[]>) => response.data)
    );
  }

  public toggleNotifications(id: string) {
    return this.http.put<IProgrammedGoalProject>(
      `${this.source}/toggle-notifications/${id}`,
      null
    );
  }
}
