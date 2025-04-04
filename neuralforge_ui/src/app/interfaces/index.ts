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
  id?: string;
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
  id?: string;
  userId?: string;
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
  default = "",
}

export enum IRoleType {
  teacher = "ROLE_TEACHER",
  student = "ROLE_STUDENT",
  admin = "ROLE_ADMINISTRATOR",
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
  };
  status: number;
}

export interface IValidationRequest {
  email: string;
  verificationCode: number | null;
}

export enum IProjectType {
  Learning = "LEARNING",
}

export interface ILearningProject {
  id: string;
  creatorUserId: string;
  createdAt?: Date;
  name: string;
  description?: string;
  projectType: IProjectType;
}

interface IDashboardCard {
  title: string;
  content: string;
  count?: number;
  id?: string | number;
  projectType: IProjectType;
}

export interface IDashboardSection {
  title: string;
  buttonText: string;
  buttonAction?: () => void;
  cards: IDashboardCard[];
  isLoading?: boolean;
  hasError?: boolean;
  errorMessage?: string;
}

export enum ProjectTypeEnum {
  PROGRAMMED_GOAL,
  LEARNING,
}


export interface IProgrammedGoalProject {
  id?: string;
  projectType: ProjectTypeEnum;
  creatorUserId?: string;
  name: string;
  description: string;
  deadline: Date;
  createdAt: Date | null;
  notify: boolean;
  selectedDays: ISelectedDays;
  dynamicContents?: IDynamicContent[];
}

export interface ISelectedDays {
  monday: boolean;
  tuesday: boolean;
  wednesday: boolean;
  thursday: boolean;
  friday: boolean;
  saturday: boolean;
  sunday: boolean;
  [key: string]: boolean;
}

export interface IDynamicContent {
  id?: string;
  title: string;
  creationDate?: Date;
  path: string;
  email: string;
  type: string;
  projectId: string;
}

export interface IDynamicContentSection {
  title: string;
  buttonText: string;
  buttonAction: () => void;
  isLoading: boolean;
  cards: IDynamicContent[];
  hasError?: boolean;
  errorMessage?: string;
}