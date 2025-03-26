
export interface ILoginResponse {
  accessToken: string;
  expiresIn: number;
}


export interface IResponse<T> {
  data: T;
  message: string;
  meta: T;
}


export interface IUser {
  id?: number;
  name?: string;
  lastname?: string;
  email?: string;
  password?: string;
  active?: boolean;
  createdAt?: string;
  authorities?: IAuthority[];
  role?: IRole;
  status?: boolean;
  verified?: boolean;
}

export interface INotification {
  id?: number;
  userId?: number;
  title?: string;
  description?: string;
  actionLabel?: string;
  redirectTo?: string;
  dismissed?: boolean;
}


export interface IAuthority {
  authority: string;
}


export interface IFeedBackMessage {
  type?: IFeedbackStatus;
  message?: string;
}


export enum IFeedbackStatus {
  success = "SUCCESS",
  error = "ERROR",
  default = ''
}


export enum IRoleType {
  teacher = "ROLE_TEACHER",
  student = "ROLE_STUDENT",
  admin = "ROLE_ADMINISTRATOR"
}


export interface IRole {
  createdAt: string;
  description: string;
  id: string;
  name: string;
}


export interface ISearch {
  page?: number;
  size?: number;
  pageNumber?: number;
  pageSize?: number;
  totalElements?: number;
  totalPages?: number;
}

export interface IExceptionResponse {
  error: {
    id: string;
    exception: [string] | string;
  }
  status: number;
}


export interface IValidationRequest {
  email: string;
  verificationCode: number | null;
}