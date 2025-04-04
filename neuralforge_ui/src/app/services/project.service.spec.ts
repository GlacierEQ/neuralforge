import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { IProjectType, ISelectedDays } from "../interfaces";
import { AllProjects, ProjectService } from "./project.service";

describe("ProjectService", () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  const BASE_URL = "api/neuralforge/v1/projects";

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService],
    });

    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should get all user projects", () => {
    const mockSelectedDays: ISelectedDays = {
      monday: true,
      tuesday: true,
      wednesday: true,
      thursday: true,
      friday: true,
      saturday: false,
      sunday: false,
    };

    const mockProjects: AllProjects = {
      learningProjects: [
        {
          id: "lp-1",
          name: "Learning Project 1",
          description: "Test",
          projectType: IProjectType.Learning,
          createdAt: new Date(),
          lastModifiedAt: null,
          materials: [],
        },
      ],
      teachingProjects: [
        {
          id: "tp-1",
          name: "Teaching Project 1",
          description: "Test",
          projectType: IProjectType.Teaching,
          selectedDays: mockSelectedDays,
          dailyHours: 2,
          weeksCount: 4,
          hoursPerClass: 1,
          materials: [],
          createdAt: new Date(),
          lastModifiedAt: null,
        },
      ],
      programmedGoalProjects: [
        {
          id: "pgp-1",
          name: "Programmed Goal Project 1",
          description: "Test",
          projectType: IProjectType.ProgrammedGoal,
          deadline: new Date(),
          notify: true,
          selectedDays: mockSelectedDays,
          materials: [],
          createdAt: new Date(),
          lastModifiedAt: null,
        },
      ],
    };

    service.getAllUserProjects().subscribe((projects) => {
      expect(projects).toEqual(mockProjects);
      expect(projects.learningProjects.length).toBe(1);
      expect(projects.teachingProjects.length).toBe(1);
      expect(projects.programmedGoalProjects.length).toBe(1);
    });

    const req = httpMock.expectOne(`${BASE_URL}/all-mine`);
    expect(req.request.method).toBe("GET");
    req.flush(mockProjects);
  });

  // Test inherited methods from BaseService
  it("should find a project by id", () => {
    const projectId = "project-123";
    const mockResponse = {
      data: { id: projectId, name: "Test Project" },
    };

    service.find(projectId).subscribe((response) => {
      expect(response.data.id).toBe(projectId);
      expect(response.data.name).toBe("Test Project");
    });

    const req = httpMock.expectOne(`${BASE_URL}/${projectId}`);
    expect(req.request.method).toBe("GET");
    req.flush(mockResponse);
  });

  it("should find all projects", () => {
    const mockResponse = {
      data: [
        { id: "project-1", name: "Project 1" },
        { id: "project-2", name: "Project 2" },
      ],
    };

    service.findAll().subscribe((response) => {
      expect(response.data.length).toBe(2);
      expect(response.data[0].name).toBe("Project 1");
      expect(response.data[1].name).toBe("Project 2");
    });

    const req = httpMock.expectOne(BASE_URL);
    expect(req.request.method).toBe("GET");
    req.flush(mockResponse);
  });

  it("should add a new project", () => {
    const newProject = { name: "New Project", description: "Test Description" };
    const mockResponse = {
      data: {
        id: "new-project-1",
        name: "New Project",
        description: "Test Description",
      },
    };

    service.add(newProject).subscribe((response) => {
      expect(response.data.name).toBe("New Project");
      expect(response.data.id).toBe("new-project-1");
    });

    const req = httpMock.expectOne(BASE_URL);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(newProject);
    req.flush(mockResponse);
  });

  it("should update a project", () => {
    const projectId = 123;
    const updatedProject = {
      name: "Updated Project",
      description: "Updated Description",
    };
    const mockResponse = {
      data: {
        id: projectId,
        name: "Updated Project",
        description: "Updated Description",
      },
    };

    service.edit(projectId, updatedProject).subscribe((response) => {
      expect(response.data.name).toBe("Updated Project");
      expect(response.data.id).toBe(projectId);
    });

    const req = httpMock.expectOne(`${BASE_URL}/${projectId}`);
    expect(req.request.method).toBe("PUT");
    expect(req.request.body).toEqual(updatedProject);
    req.flush(mockResponse);
  });

  it("should delete a project", () => {
    const projectId = "project-to-delete";
    const mockResponse = {
      data: { message: "Project deleted successfully" },
    };

    service.del(projectId).subscribe((response) => {
      expect(response.data.message).toBe("Project deleted successfully");
    });

    const req = httpMock.expectOne(`${BASE_URL}/${projectId}`);
    expect(req.request.method).toBe("DELETE");
    req.flush(mockResponse);
  });
});
