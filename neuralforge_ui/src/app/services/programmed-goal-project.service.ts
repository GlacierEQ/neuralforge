import {inject, Injectable} from "@angular/core";
import {INotification, IProgrammedGoalProject, IResponse} from "../interfaces";
import { BaseService } from "./base-service";
import { Observable } from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class ProgrammedGoalProjectService {
  private http = inject(HttpClient);
  private baseUrl = 'api/neuralforge/v1/programmed-goal-projects';
  public getById(id: string): Observable<IProgrammedGoalProject> {
    return this.http.get<IProgrammedGoalProject>(`${this.baseUrl}/${id}`);
  }

  public delete(id: string): Observable<undefined> {
    return this.http.delete<undefined>(`${this.baseUrl}/${id}`);
  }

  public add(project: IProgrammedGoalProject): Observable<IProgrammedGoalProject> {
    return this.http.post<IProgrammedGoalProject>(`${this.baseUrl}`,project);
  }

  public update(project: IProgrammedGoalProject): Observable<IProgrammedGoalProject> {
    return this.http.put<IProgrammedGoalProject>(`${this.baseUrl}/${project.id}`,project);
  }

  public findMine() {
    return this.http.get<IProgrammedGoalProject>(`${this.baseUrl}/mine`);
  }

  public toggleNotifications(id:string) {
    return this.http.put<IProgrammedGoalProject>(`${this.baseUrl}/toggle-notifications/${id}`, null);
  }
}
