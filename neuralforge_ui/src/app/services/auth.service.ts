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

/**
 * Authentication service for managing user login, roles, and session persistence.
 */
@Injectable({
  providedIn: "root",
})
export class AuthService {
  /** User's access token. */
  private accessToken!: string;

  /** Token expiration time. */
  private expiresIn!: number;

  /** Authenticated user details. */
  private userRole: IRole = {
    name: "",
    createdAt: "",
    id: "",
    description: "",
  };
  private user: IUser = { email: "", role: this.userRole };

  /** Injected HTTP client for API requests. */
  private http: HttpClient = inject(HttpClient);

  constructor() {
    this.load();
  }

  /**
   * Saves user authentication details to local storage.
   */
  public save(): void {
    if (this.user) localStorage.setItem("auth_user", JSON.stringify(this.user));

    if (this.accessToken)
      localStorage.setItem("access_token", this.accessToken);

    if (this.expiresIn)
      localStorage.setItem("expiresIn", JSON.stringify(this.expiresIn));
  }

  /**
   * Loads user authentication details from local storage.
   */
  private load(): void {
    let token = localStorage.getItem("access_token");
    if (token) this.accessToken = token;

    let exp = localStorage.getItem("expiresIn");
    if (exp) this.expiresIn = JSON.parse(exp);

    const user = localStorage.getItem("auth_user");
    if (user) this.user = JSON.parse(user);
  }

  /**
   * Retrieves the authenticated user.
   * @returns The user object or `undefined` if no user is authenticated.
   */
  public getUser(): IUser | undefined {
    return this.user;
  }

  /**
   * Retrieves the access token of the authenticated user.
   * @returns The access token or `null` if no user is authenticated.
   */
  public getAccessToken(): string | null {
    return this.accessToken;
  }

  /**
   * Checks if a user is authenticated.
   * @returns `true` if a user is logged in, `false` otherwise.
   */
  public check(): boolean {
    return !!this.accessToken;
  }

  /**
   * Authenticates the user by sending credentials to the API.
   * @param credentials User credentials (email and password).
   * @returns An `Observable` containing the login response.
   */
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

  /**
   * Checks if the user has a specific role.
   * @param role The role to check.
   * @returns `true` if the user has the role, `false` otherwise.
   */
  public hasRole(role: string): boolean {
    return this.user.role?.name === role;
  }

  /**
   * Checks if the user is a super administrator.
   * @returns `true` if the user has the admin role, `false` otherwise.
   */
  public isSuperAdmin(): boolean {
    return this.user.role?.name === IRoleType.admin;
  }

  /**
   * Checks if the user has at least one of the specified roles.
   * @param roles List of roles to check.
   * @returns `true` if the user has at least one of the roles, `false` otherwise.
   */
  public hasAnyRole(roles: any[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  /**
   * Filters the permitted routes for the user based on their roles.
   * @param routes List of available routes.
   * @returns An array of routes the user is allowed to access.
   */
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

  /**
   * Registers a new user in the system.
   * @param user User data for registration.
   * @returns An `Observable` containing the signup response.
   */
  public signup(user: IUser): Observable<ILoginResponse> {
    return this.http.post<ILoginResponse>(
      "api/neuralforge/v1/auth/register",
      user
    );
  }
  /**
   * Verifies a user's identity by providing an email and a validation code.
   * @param validationRequest Data required for validation.
   * @returns An `Observable` containing the validation response.
   */
  public verify(validationRequest: IValidationRequest) {
    return this.http.post<ILoginResponse>(
      "api/neuralforge/v1/auth/verify",
      validationRequest
    );
  }

  /**
   * Sends a password reset request to the API.
   * @param email The user's email address.
   * @returns An `Observable` containing the response.
   */
  public requestPasswordReset(email: string): Observable<string> {
    return this.http.post(
      "api/neuralforge/v1/auth/request",
      { email },
      { responseType: "text" }
    );
  }

  /**
   * Sends a password reset request to the API.
   * @param userId The user's ID from the URL.
   * @param newPassword The new password chosen by the user.
   * @returns An `Observable` containing the response from the server.
   */
  public resetPassword(token: string, newPassword: string): Observable<string> {
    return this.http.post(
      "api/neuralforge/v1/auth/reset",
      { token, newPassword },
      { responseType: "text" }
    );
  }

  /**
   * Sends the Google ID token to the API for authentication.
   * @param token The Google ID token.
   * @returns An `Observable` containing the API response.
   */

  /**
   * Logs out the user and removes their authentication data from local storage.
   */
  public logout(): void {
    this.accessToken = "";
    localStorage.removeItem("access_token");
    localStorage.removeItem("expiresIn");
    localStorage.removeItem("auth_user");
  }

  /**
   * Retrieves the user's authorities (permissions).
   * @returns An array of `IAuthority` objects or `undefined` if no authorities are available.
   */
  public getUserAuthorities(): IRole | undefined {
    return this.getUser()?.role;
  }

  /**
   * Checks if the user has the necessary permissions for a given route.
   * @param routeAuthorities List of required permissions for the route.
   * @returns `true` if the user has the required permissions, `false` otherwise.
   */
  public areActionsAvailable(routeAuthorities: string[]): boolean {
    // Validation variables
    let allowedUser: boolean = false;
    let isAdmin: boolean = false;

    // Retrieve user authorities
    let userRole = this.getUserAuthorities();

    // Check if the user is permitted for the given route
    for (const authority of routeAuthorities) {
      if (userRole?.name == authority) {
        allowedUser = true;
        break;
      }
    }

    // Check if the user has admin privileges
    if (userRole?.name == IRoleType.admin) {
      isAdmin = true;
    }

    return allowedUser && isAdmin;
  }

  /**
   * Retrieves the current authenticated user's information from the API.
   *
   * @returns An Observable containing the user data.
   */
  public getCurrentUser(): Observable<any> {
    return this.http.get<any>("api/neuralforge/v1/auth/me");
  }
}
