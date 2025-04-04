import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { ProjectMaterial } from "../models/project-material.model";
import {
  MaterialUpdate,
  ProjectMaterialService,
} from "./project-material.service";

describe("ProjectMaterialService", () => {
  let service: ProjectMaterialService;
  let httpMock: HttpTestingController;
  const BASE_URL = "api/neuralforge/v1/project-materials";

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectMaterialService],
    });

    service = TestBed.inject(ProjectMaterialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should get project materials", () => {
    const projectId = "project-123";
    const mockMaterials: ProjectMaterial[] = [
      {
        id: "material-1",
        type: "file",
        fileName: "test.pdf",
        projectId,
        description: "Test File",
        createdAt: new Date(),
      },
      {
        id: "material-2",
        type: "hyperlink",
        hyperlink: "https://example.com",
        projectId,
        description: "Test Link",
        createdAt: new Date(),
      },
    ];

    service.getProjectMaterials(projectId).subscribe((materials) => {
      expect(materials).toEqual(mockMaterials);

      // Check if materials were cached properly
      service.getMaterialsForProject(projectId).subscribe((cachedMaterials) => {
        expect(cachedMaterials).toEqual(mockMaterials);
      });
    });

    const req = httpMock.expectOne(`${BASE_URL}/project/${projectId}`);
    expect(req.request.method).toBe("GET");
    req.flush(mockMaterials);
  });

  it("should upload material and update cache", () => {
    const projectId = "project-123";
    const formData = new FormData();
    const mockMaterial: ProjectMaterial = {
      id: "material-1",
      type: "file",
      fileName: "test.pdf",
      projectId,
      description: "Test File",
      createdAt: new Date(),
    };

    service.uploadMaterial(formData).subscribe((material) => {
      expect(material).toEqual(mockMaterial);
    });

    // Also check that materialUpdates emits correct update
    service.materialUpdates$.subscribe((update) => {
      if (update) {
        expect(update.projectId).toBe(projectId);
        expect(update.type).toBe("add");
        expect(update.material).toEqual(mockMaterial);
      }
    });

    const req = httpMock.expectOne(`${BASE_URL}/upload`);
    expect(req.request.method).toBe("POST");
    req.flush(mockMaterial);
  });

  it("should delete material and update cache", () => {
    const projectId = "project-123";
    const materialId = "material-1";

    // First, populate cache with a material
    const mockMaterials: ProjectMaterial[] = [
      {
        id: materialId,
        type: "file",
        fileName: "test.pdf",
        projectId,
        description: "Test File",
        createdAt: new Date(),
      },
    ];

    service["updateMaterialsCache"](projectId, mockMaterials);

    // Then delete the material
    service.deleteMaterial(materialId).subscribe();

    // Check that materialUpdates emits correct update
    service.materialUpdates$.subscribe((update) => {
      if (update && update.material?.id === materialId) {
        expect(update.projectId).toBe(projectId);
        expect(update.type).toBe("delete");
      }
    });

    // Check that material was removed from cache
    service.getMaterialsForProject(projectId).subscribe((materials) => {
      expect(materials.length).toBe(0);
    });

    const req = httpMock.expectOne(`${BASE_URL}/${materialId}`);
    expect(req.request.method).toBe("DELETE");
    req.flush(null);
  });

  it("should download material file", () => {
    const materialId = "material-1";
    const mockBlob = new Blob(["test file content"], {
      type: "application/pdf",
    });

    service.downloadMaterialFile(materialId).subscribe((blob) => {
      expect(blob).toEqual(mockBlob);
    });

    const req = httpMock.expectOne(`${BASE_URL}/download/${materialId}`);
    expect(req.request.method).toBe("GET");
    expect(req.request.responseType).toBe("blob");
    req.flush(mockBlob);
  });

  it("should notify material update with add type", () => {
    const projectId = "project-123";
    const mockMaterial: ProjectMaterial = {
      id: "material-1",
      type: "file",
      fileName: "test.pdf",
      projectId,
      description: "Test File",
      createdAt: new Date(),
    };

    const update: MaterialUpdate = {
      projectId,
      type: "add",
      material: mockMaterial,
    };

    // Pre-initialize cache with empty array to prevent auto-fetch
    const initialMap = new Map<string, ProjectMaterial[]>();
    initialMap.set(projectId, []);
    service["materialsByProjectSubject"].next(initialMap);

    // Spy on getProjectMaterials to prevent HTTP requests
    jest.spyOn(service, "getProjectMaterials").mockReturnValue(of([]));

    service.notifyMaterialUpdate(update);

    // Check material updates emission
    service.materialUpdates$.subscribe((emittedUpdate) => {
      if (emittedUpdate) {
        expect(emittedUpdate).toEqual(update);
      }
    });

    // Directly check the cache
    const materials =
      service["materialsByProjectSubject"].value.get(projectId) || [];
    expect(materials.length).toBe(1);
    expect(materials[0]).toEqual(mockMaterial);
  });

  it("should notify material update with delete type", () => {
    const projectId = "project-123";
    const mockMaterial: ProjectMaterial = {
      id: "material-1",
      type: "file",
      fileName: "test.pdf",
      projectId,
      description: "Test File",
      createdAt: new Date(),
    };

    // Pre-initialize cache with the material
    const initialMap = new Map<string, ProjectMaterial[]>();
    initialMap.set(projectId, [mockMaterial]);
    service["materialsByProjectSubject"].next(initialMap);

    // Spy on getProjectMaterials to prevent HTTP requests
    jest.spyOn(service, "getProjectMaterials").mockReturnValue(of([]));

    const update: MaterialUpdate = {
      projectId,
      type: "delete",
      material: mockMaterial,
    };

    service.notifyMaterialUpdate(update);

    // Check material updates emission
    service.materialUpdates$.subscribe((emittedUpdate) => {
      if (emittedUpdate) {
        expect(emittedUpdate).toEqual(update);
      }
    });

    // Directly check the cache
    const materials =
      service["materialsByProjectSubject"].value.get(projectId) || [];
    expect(materials.length).toBe(0);
  });

  it("should get materials for project from cache if available", () => {
    const projectId = "project-123";
    const mockMaterials: ProjectMaterial[] = [
      {
        id: "material-1",
        type: "file",
        fileName: "test.pdf",
        projectId,
        description: "Test File",
        createdAt: new Date(),
      },
    ];

    // Populate cache
    service["updateMaterialsCache"](projectId, mockMaterials);

    // Should get from cache without HTTP request
    service.getMaterialsForProject(projectId).subscribe((materials) => {
      expect(materials).toEqual(mockMaterials);
    });

    httpMock.expectNone(`${BASE_URL}/project/${projectId}`);
  });

  it("should fetch from API when cache is empty for project", () => {
    const projectId = "project-123";
    const mockMaterials: ProjectMaterial[] = [
      {
        id: "material-1",
        type: "file",
        fileName: "test.pdf",
        projectId,
        description: "Test File",
        createdAt: new Date(),
      },
    ];

    // Call getMaterialsForProject without populating cache first
    service.getMaterialsForProject(projectId).subscribe();

    const req = httpMock.expectOne(`${BASE_URL}/project/${projectId}`);
    expect(req.request.method).toBe("GET");
    req.flush(mockMaterials);

    // Verify the cache was updated
    service.getMaterialsForProject(projectId).subscribe((materials) => {
      expect(materials).toEqual(mockMaterials);
    });
  });
});
