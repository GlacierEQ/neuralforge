import { inject, Injectable, signal } from "@angular/core";
import { ISearch, IUser } from "../interfaces";
import { AlertService } from "./alert.service";
import { BaseService } from "./base-service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: "root",
})
export class UserService extends BaseService<IUser> {
  protected override source: string = "api/neuralforge/v1/users";
  private userListSignal = signal<IUser[]>([]);

  get users$() {
    return this.userListSignal;
  }

  public search: ISearch = {
    page: 1,
    size: 5,
  };
  public totalItems: any = [];
  private alertService: AlertService = inject(AlertService);

  getAll() {
    this.findAllWithParams({
      page: this.search.page,
      size: this.search.size,
    }).subscribe({
      next: (response: any) => {
        this.search = { ...this.search, ...response.meta };
        this.totalItems = Array.from(
          { length: this.search.totalPages ? this.search.totalPages : 0 },
          (_, i) => i + 1
        );
        this.userListSignal.set(response);
      },
      error: (err: any) => {
        console.error("error", err);
      },
    });
  }

  block(user: IUser){

  }
  toggleStatus(userId: string): Observable<any> {
    return this.http.put(`${this.source}/${userId}/toggle-status`, {});
  }

  getAllRoles() {
    return this.http.get<any[]>(`api/neuralforge/v1/roles`);
  }

  updateUserRole(userId: string, roleId: string) {
    console.log(roleId, userId)
    return this.http.put(`${this.source}/${userId}/role`,  roleId ); // or PATCH
  }

  save(user: IUser) {
    this.add(user).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
          "success",
          response.message,
          "center",
          "top",
          ["success-snackbar"]
        );
        this.getAll();
      },
      error: (err: any) => {
        this.alertService.displayAlert(
          "error",
          "An error occurred adding the user",
          "center",
          "top",
          ["error-snackbar"]
        );
        console.error("error", err);
      },
    });
  }

  update(user: IUser) {
    this.editCustomSource(`${user.id}`, user).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
          "success",
          response.message,
          "center",
          "top",
          ["success-snackbar"]
        );
        this.getAll();
      },
      error: (err: any) => {
        this.alertService.displayAlert(
          "error",
          "An error occurred updating the user",
          "center",
          "top",
          ["error-snackbar"]
        );
        console.error("error", err);
      },
    });
  }

  delete(user: IUser) {
    this.delCustomSource(`${user.id}`).subscribe({
      next: (response: any) => {
        this.alertService.displayAlert(
          "success",
          response.message,
          "center",
          "top",
          ["success-snackbar"]
        );
        this.getAll();
      },
      error: (err: any) => {
        this.alertService.displayAlert(
          "error",
          "An error occurred deleting the user",
          "center",
          "top",
          ["error-snackbar"]
        );
        console.error("error", err);
      },
    });
  }
}
