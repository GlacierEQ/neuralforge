import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import {
  ILearningProject,
  IProgrammedGoalProject,
  ITeachingProject,
} from "../interfaces";
import { BaseService } from "./base-service";

export interface AllProjects {
  learningProjects: ILearningProject[];
  teachingProjects: ITeachingProject[];
  programmedGoalProjects: IProgrammedGoalProject[];
}

@Injectable({
  providedIn: "root",
})
export class ProjectService extends BaseService<any> {
  protected override source = "api/neuralforge/v1/projects";

  /**
   * Fetches all project types for the current user with a single API call
   * @returns Observable with all projects for the user
   */
  getAllUserProjects(): Observable<AllProjects> {
    return this.http.get<AllProjects>(`${this.source}/all-mine`);
  }

  getAllProjects(): Observable<AllProjects> {
    return this.http.get<AllProjects>(`${this.source}/all`);
  }
}
