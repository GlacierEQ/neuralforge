/**
 * Represents the response from a login request.
 */
export interface ILoginResponse {
  accessToken: string; // Token received upon successful authentication.
  expiresIn: number; // Token expiration time in seconds.
}

/**
 * Generic response interface for API requests.
 * @template T - The type of the response data.
 */
export interface IResponse<T> {
  data: T; // The actual data returned by the API.
  message: string; // A message from the API (e.g., success or error messages).
  meta: T; // Additional metadata related to the response.
}

/**
 * Represents a user in the system.
 */
export interface IUser {
  id?: number; // Unique identifier of the user.
  name?: string; // First name of the user.
  lastname?: string; // Last name of the user.
  email?: string; // Email address of the user.
  password?: string; // User's password (should be handled securely).
  active?: boolean; // Indicates if the user account is active.
  createdAt?: string; // Timestamp when the user was created.
  authorities?: IAuthority[]; // List of roles or permissions assigned to the user.
  role?: IRole; // The specific role of the user.
  status?: boolean; // Indicates if the user is able to log in.
  verified?: boolean; // Indicates if the user is verified.
}

/**
 * Represents an authority or permission assigned to a user.
 */
export interface IAuthority {
  authority: string; // The name of the authority/permission.
}

/**
 * Represents a feedback message structure.
 */
export interface IFeedBackMessage {
  type?: IFeedbackStatus; // The type of feedback message.
  message?: string; // The message content.
}

/**
 * Enum representing different feedback message statuses.
 */
export enum IFeedbackStatus {
  success = "SUCCESS", // Indicates a successful operation.
  error = "ERROR", // Indicates an error occurred.
  default = '' // Default status (empty).
}

/**
 * Enum representing different user role types in the system.
 */
export enum IRoleType {
  teacher = "ROLE_TEACHER", // Role for teachers.
  student = "ROLE_STUDENT", // Role for students.
  admin = "ROLE_ADMINISTRATOR" // Role for administrators.
}

/**
 * Represents a user role with detailed information.
 */
export interface IRole {
  createdAt: string; // Timestamp when the role was created.
  description: string; // Description of the role.
  id: string; // Unique identifier for the role.
  name: string; // The name of the role.
}

/**
 * Represents pagination details for API searches.
 */
export interface ISearch {
  page?: number; // Current page number.
  size?: number; // Number of items per page.
  pageNumber?: number; // Alias for the current page number.
  pageSize?: number; // Alias for the number of items per page.
  totalElements?: number; // Total number of elements in the search result.
  totalPages?: number; // Total number of available pages.
}
/**
 * Represents an Exception thrown by the NeuralForge API.
 */
export interface IExceptionResponse {
  error: {
    id: string;
    exception: [string] | string;
  }
  status: number;
}

/**
 * Represents the validation request.
 */
export interface IValidationRequest {
  email: string;
  verificationCode: number | null;
}