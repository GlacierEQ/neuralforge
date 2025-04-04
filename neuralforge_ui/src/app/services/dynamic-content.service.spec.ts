import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { IDynamicContent } from "../interfaces";
import { DynamicContentService } from "./dynamic-content.service";

describe("DynamicContentService", () => {
  let service: DynamicContentService;
  let httpMock: HttpTestingController;
  const BASE_URL = "api/neuralforge/v1/DynamicContent";

  const mockDynamicContent: IDynamicContent = {
    id: "dc-1",
    title: "Test Content",
    type: "pdf",
    creationDate: new Date(),
    path: "/path/to/content",
    email: "test@example.com",
    projectId: "project-1",
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DynamicContentService],
    });

    service = TestBed.inject(DynamicContentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should get dynamic content by project id", () => {
    const projectId = "project-1";
    const mockContents: IDynamicContent[] = [mockDynamicContent];

    service.getByProjectId(projectId).subscribe((contents) => {
      expect(contents).toEqual(mockContents);
      expect(contents.length).toBe(1);
      expect(contents[0].projectId).toBe(projectId);
    });

    const req = httpMock.expectOne(`${BASE_URL}/project/${projectId}`);
    expect(req.request.method).toBe("GET");
    req.flush(mockContents);
  });

  it("should generate content", () => {
    const projectId = "project-1";
    const materialId = "material-1";
    const title = "Generated Content";
    const type = "pdf";

    service
      .generateContent(projectId, materialId, title, type)
      .subscribe((response) => {
        expect(response).toBeUndefined();
      });

    const req = httpMock.expectOne(`${BASE_URL}/generate`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({
      projectId,
      materialId,
      title,
      type,
    });
    req.flush(null);
  });

  it("should download content as array buffer", () => {
    const contentId = "dc-1";
    const mockArrayBuffer = new ArrayBuffer(8);

    service.download(contentId).subscribe((buffer) => {
      expect(buffer).toEqual(mockArrayBuffer);
    });

    const req = httpMock.expectOne(`${BASE_URL}/download/${contentId}`);
    expect(req.request.method).toBe("GET");
    expect(req.request.responseType).toBe("arraybuffer");
    req.flush(mockArrayBuffer);
  });

  // Test inherited methods from BaseService
  it("should find all dynamic content", () => {
    const mockResponse = {
      data: [mockDynamicContent],
      message: "Success",
      meta: null,
    };

    service.findAll().subscribe((response) => {
      expect(response.data).toEqual([mockDynamicContent]);
    });

    const req = httpMock.expectOne(BASE_URL);
    expect(req.request.method).toBe("GET");
    req.flush(mockResponse);
  });

  it("should add new dynamic content", () => {
    const newContent = {
      title: "New Content",
      type: "pdf",
      projectId: "project-1",
    };

    const createdContent = {
      ...mockDynamicContent,
      title: "New Content",
      id: "new-dc-1",
    };

    const mockResponse = {
      data: createdContent,
      message: "Created successfully",
      meta: null,
    };

    service.add(newContent).subscribe((response) => {
      expect(response.data.id).toBe("new-dc-1");
      expect(response.data.title).toBe("New Content");
    });

    const req = httpMock.expectOne(BASE_URL);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(newContent);
    req.flush(mockResponse);
  });

  it("should delete dynamic content", () => {
    const contentId = "dc-1";
    const mockResponse = {
      data: null,
      message: "Deleted successfully",
      meta: null,
    };

    service.del(contentId).subscribe((response) => {
      expect(response.message).toBe("Deleted successfully");
    });

    const req = httpMock.expectOne(`${BASE_URL}/${contentId}`);
    expect(req.request.method).toBe("DELETE");
    req.flush(mockResponse);
  });
});
