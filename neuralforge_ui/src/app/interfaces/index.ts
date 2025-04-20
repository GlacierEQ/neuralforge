import { ProjectMaterial } from "../models/project-material.model";

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
  lastName?: string;
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

export interface IVirtualStudent {
  id: string;
  userId: string;
  grade: number;
}

export interface IVirtualWeek {
  id: string;
  topic: string;
  startDate: Date;
  endDate: Date;
  content: string;
  visible: boolean;
}

export interface IVirtualClass {
  id: string;
  name: string;
  description: string;
  title: string;
  creatorUserId: string;
  studentIds: string[];
  createdAt: Date;
  updatedAt: Date;
  weeks?: IVirtualWeek[];
  students?: IVirtualStudent[];
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
  Teaching = "TEACHING",
  ProgrammedGoal = "PROGRAMMED_GOAL",
}

export interface IProject {
  id: string;
  projectType: IProjectType;
  creatorUserId?: string;
  name: string;
  description: string;
  createdAt: Date;
  lastModifiedAt: Date | null;
  materials: ProjectMaterial[];
}

export interface ILearningProject extends IProject {}

export interface IProgrammedGoalProject extends IProject {
  deadline: Date;
  notify: boolean;
  selectedDays: ISelectedDays;
  dynamicContents?: IDynamicContent[];
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
  id: string;
  title: string;
  type: string;
  creationDate: Date;
  path: string;
  email: string;
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
  projectId: string;
}

export interface ILearningMaterial {
  id?: string;
  name: string;
  description?: string;
  url?: string;
  type: string;
  createdAt?: Date;
}

export interface ITeachingProject extends IProject {
  selectedDays: ISelectedDays;
  dailyHours: number;
  weeksCount: number;
  hoursPerClass: number;
  materials: ProjectMaterial[];
  startDate?: Date;
  endDate?: Date;
  weeks?: ICourseWeek[];
}

export interface ICourseWeek {
  id: string;
  weekNumber: number;
  classSessions: IClassSession[];
}

export interface IClassSession {
  id: string;
  dayOfWeek: string;
  topics: ICourseTopic[];
}

export interface ICourseTopic {
  id: string;
  title: string;
  description: string;
  orderIndex: number;
  durationMinutes: number;
  teacherLocked: boolean;
  sourceMaterials: ProjectMaterial[];
  sourceReferences: { [key: string]: string };
}
