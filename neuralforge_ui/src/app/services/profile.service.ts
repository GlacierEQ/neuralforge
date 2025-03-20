import { Injectable, signal } from "@angular/core";
import { Observable, tap, catchError, throwError } from "rxjs";
import { IUser } from "../interfaces";
import { BaseService } from "./base-service";

@Injectable({
  providedIn: "root",
})
export class ProfileService extends BaseService<IUser> {
  protected override source: string = "api/neuralforge/v1/users/profile";
  private userNameSignal = signal<string>("");
  public userName = this.userNameSignal.asReadonly();

  constructor() {
    super();
    this.loadFromStorage();
  }

  private loadFromStorage(): void {
    const userData = localStorage.getItem("auth_user");
    if (userData) {
      const user = JSON.parse(userData);
      this.userNameSignal.set(user.name || "");
    }
  }

  updateUserName(name: string): void {
    this.userNameSignal.set(name);
  }

  refreshFromApi(user: IUser): void {
    if (user && user.name) {
      this.userNameSignal.set(user.name);
    }
  }

  updateUserProfile(userData: {
    name: string;
    lastName: string;
  }): Observable<any> {
    return this.http.put<any>(this.source, userData).pipe(
      tap((updatedUser) => {
        const currentUser = localStorage.getItem("auth_user");
        if (currentUser) {
          const user = JSON.parse(currentUser);

          user.name = updatedUser.name;
          user.lastName = updatedUser.lastName;

          localStorage.setItem("auth_user", JSON.stringify(user));

          this.updateUserName(updatedUser.name);
        }
      })
    );
  }

  deleteAccount(): Observable<any> {
    return this.http.delete(this.source).pipe(
      catchError((error) => {
        console.error("Error deleting account", error);
        return throwError(() => error);
      })
    );
  }
}
