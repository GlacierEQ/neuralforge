import { Injectable } from "@angular/core";
import { ILearningProject } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class LearningProjectService extends BaseService<ILearningProject> {
  protected override source = "api/neuralforge/v1/learning-projects";
}
