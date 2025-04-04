import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import {
  IProjectType,
  IResponse,
  ISelectedDays,
  ITeachingProject,
} from "../interfaces";
import { TeachingProjectService } from "./teaching-project.service";

describe("TeachingProjectService", () => {
  let service: TeachingProjectService;
  let httpMock: HttpTestingController;
  const BASE_URL = "api/neuralforge/v1/teaching-projects";

  const mockSelectedDays: ISelectedDays = {
    monday: true,
    tuesday: true,
    wednesday: true,
    thursday: true,
    friday: true,
    saturday: false,
    sunday: false,
  };

  const mockTeachingProject: ITeachingProject = {
    id: "tp-1",
    name: "Test Teaching Project",
    description: "Test Description",
    projectType: IProjectType.Teaching,
    selectedDays: mockSelectedDays,
    dailyHours: 2,
    weeksCount: 4,
    hoursPerClass: 1,
    materials: [],
    createdAt: new Date(),
    lastModifiedAt: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeachingProjectService],
    });

    service = TestBed.inject(TeachingProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should get teaching project by id", () => {
    const projectId = "tp-1";

    service.getById(projectId).subscribe((project) => {
      expect(project).toEqual(mockTeachingProject);
      expect(project.id).toBe(projectId);
    });

    const req = httpMock.expectOne(`${BASE_URL}/${projectId}`);
    expect(req.request.method).toBe("GET");
    req.flush(mockTeachingProject);
  });

  it("should update teaching project", () => {
    const updatedProject = { ...mockTeachingProject, name: "Updated Name" };

    service.update(updatedProject).subscribe((project) => {
      expect(project).toEqual(updatedProject);
      expect(project.name).toBe("Updated Name");
    });

    const req = httpMock.expectOne(`${BASE_URL}/${updatedProject.id}`);
    expect(req.request.method).toBe("PUT");
    expect(req.request.body).toEqual(updatedProject);
    req.flush(updatedProject);
  });

  it("should find my teaching projects", () => {
    const mockProjects: ITeachingProject[] = [mockTeachingProject];
    const mockResponse: IResponse<ITeachingProject[]> = {
      data: mockProjects,
      message: "Success",
      meta: {} as any,
    };

    service.findMine().subscribe((projects) => {
      expect(projects).toEqual(mockProjects);
      expect(projects.length).toBe(1);
    });

    const req = httpMock.expectOne(`${BASE_URL}/mine`);
    expect(req.request.method).toBe("GET");
    req.flush(mockResponse);
  });

  it("should delete a teaching project", () => {
    const projectId = "tp-1";

    service.delete(projectId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`${BASE_URL}/${projectId}`);
    expect(req.request.method).toBe("DELETE");
    req.flush(null);
  });

  // Test inherited methods from BaseService
  it("should find all teaching projects with params", () => {
    const mockProjects: ITeachingProject[] = [mockTeachingProject];
    const mockResponse: IResponse<ITeachingProject[]> = {
      data: mockProjects,
      message: "Success",
      meta: {} as any,
    };

    const params = { page: 1, size: 10 };

    service.findAllWithParams(params).subscribe((response) => {
      expect(response.data).toEqual(mockProjects);
    });

    const req = httpMock.expectOne(`${BASE_URL}?page=1&size=10`);
    expect(req.request.method).toBe("GET");
    req.flush(mockResponse);
  });

  it("should add a new teaching project", () => {
    const newProject = { ...mockTeachingProject, id: undefined };
    delete newProject.id; // Remove id for creation

    const createdProject = { ...mockTeachingProject, id: "new-tp-1" };
    const mockResponse: IResponse<ITeachingProject> = {
      data: createdProject,
      message: "Success",
      meta: {} as any,
    };

    service.add(newProject).subscribe((response) => {
      expect(response.data.id).toBe("new-tp-1");
    });

    const req = httpMock.expectOne(BASE_URL);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(newProject);
    req.flush(mockResponse);
  });
});
