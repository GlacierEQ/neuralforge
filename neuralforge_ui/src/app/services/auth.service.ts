import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, Observable, tap, throwError } from "rxjs";
import {
  ILoginResponse,
  IRole,
  IRoleType,
  IUser,
  IValidationRequest,
} from "../interfaces";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private accessToken!: string;

  private expiresIn!: number;

  private userRole: IRole = {
    name: "",
    createdAt: "",
    id: "",
    description: "",
  };
  private user: IUser = { email: "", role: this.userRole };

  private http: HttpClient = inject(HttpClient);

  constructor() {
    this.load();
  }

  public save(): void {
    if (this.user) localStorage.setItem("auth_user", JSON.stringify(this.user));

    if (this.accessToken)
      localStorage.setItem("access_token", this.accessToken);

    if (this.expiresIn)
      localStorage.setItem("expiresIn", JSON.stringify(this.expiresIn));
  }

  private load(): void {
    let token = localStorage.getItem("access_token");
    if (token) this.accessToken = token;

    let exp = localStorage.getItem("expiresIn");
    if (exp) this.expiresIn = JSON.parse(exp);

    const user = localStorage.getItem("auth_user");
    if (user) this.user = JSON.parse(user);
  }

  public getUser(): IUser | undefined {
    return this.user;
  }

  public getAccessToken(): string | null {
    return this.accessToken;
  }

  public check(): boolean {
    return !!this.accessToken;
  }

  public login(credentials: {
    email: string;
    password: string;
  }): Observable<ILoginResponse> {
    return this.http
      .post<ILoginResponse>("api/neuralforge/v1/auth/login", credentials)
      .pipe(
        tap((response: any) => {
          this.accessToken = response.token;
          this.user.email = credentials.email;
          this.expiresIn = response.expiresIn;
          this.user = response.authUser;
          this.save();
        })
      );
  }
  public sendGoogleTokenToApi(token: string): Observable<ILoginResponse> {
    return this.http
      .post<ILoginResponse>("api/neuralforge/v1/auth/google-auth", token, {
        headers: { "Content-Type": "text/plain" },
      })
      .pipe(
        tap((response: any) => {
          this.accessToken = response.token;
          this.user.email = response.authUser.email;
          this.expiresIn = response.expiresIn;
          this.user = response.authUser;
          this.save();
        }),
        catchError((error: HttpErrorResponse) => {
          let errorMessage = "An error occurred during authentication.";

          if (error.status === 401 && error.error?.exception) {
            errorMessage = error.error.exception;
          }

          console.error("Authentication error:", error);
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  public hasRole(role: string): boolean {
    return this.user.role?.name === role;
  }

  public isSuperAdmin(): boolean {
    return this.user.role?.name === IRoleType.admin;
  }

  public hasAnyRole(roles: any[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  public getPermittedRoutes(routes: any[]): any[] {
    let permittedRoutes: any[] = [];
    for (const route of routes) {
      if (route.data && route.data.authorities) {
        if (this.hasAnyRole(route.data.authorities)) {
          permittedRoutes.unshift(route);
        }
      }
    }
    return permittedRoutes;
  }

  public signup(user: IUser): Observable<ILoginResponse> {
    return this.http.post<ILoginResponse>(
      "api/neuralforge/v1/auth/register",
      user
    );
  }

  public verify(validationRequest: IValidationRequest) {
    return this.http.post<ILoginResponse>(
      "api/neuralforge/v1/auth/verify",
      validationRequest
    );
  }

  public requestPasswordReset(email: string): Observable<Object> {
    return this.http.post("api/neuralforge/v1/auth/request", { email });
  }

  public resetPassword(token: string, newPassword: string): Observable<string> {
    return this.http.post(
      "api/neuralforge/v1/auth/reset",
      { token, newPassword },
      { responseType: "text" }
    );
  }

  public logout(): void {
    this.accessToken = "";
    localStorage.removeItem("access_token");
    localStorage.removeItem("expiresIn");
    localStorage.removeItem("auth_user");
  }

  public getUserAuthorities(): IRole | undefined {
    return this.getUser()?.role;
  }

  public areActionsAvailable(routeAuthorities: string[]): boolean {
    let allowedUser: boolean = false;
    let isAdmin: boolean = false;

    let userRole = this.getUserAuthorities();

    for (const authority of routeAuthorities) {
      if (userRole?.name == authority) {
        allowedUser = true;
        break;
      }
    }

    if (userRole?.name == IRoleType.admin) {
      isAdmin = true;
    }

    return allowedUser && isAdmin;
  }

  public getCurrentUser(): Observable<any> {
    return this.http.get<any>("api/neuralforge/v1/auth/me");
  }
}
