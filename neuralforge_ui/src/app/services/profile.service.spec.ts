import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { IUser } from "../interfaces";
import { ProfileService } from "./profile.service";

describe("ProfileService", () => {
  let service: ProfileService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProfileService],
    });

    service = TestBed.inject(ProfileService);
    httpMock = TestBed.inject(HttpTestingController);

    // Mock localStorage
    const mockLocalStorage = (() => {
      let store: Record<string, string> = {};
      return {
        getItem: (key: string) => store[key] || null,
        setItem: (key: string, value: string) => {
          store[key] = value;
        },
        clear: () => {
          store = {};
        },
        removeItem: (key: string) => {
          delete store[key];
        },
      };
    })();

    Object.defineProperty(window, "localStorage", {
      value: mockLocalStorage,
      writable: true,
    });

    // Prepopulate localStorage for tests
    localStorage.setItem(
      "auth_user",
      JSON.stringify({ name: "John", lastName: "Doe" })
    );
  });

  afterEach(() => {
    httpMock.verify();
    jest.clearAllMocks();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should load user name from localStorage on initialization", () => {
    expect(service.userName()).toEqual("John");
  });

  it("should update userName correctly", () => {
    service.updateUserName("Jane");
    expect(service.userName()).toEqual("Jane");
  });

  it("should refresh user name from API data", () => {
    const mockUser: IUser = {
      name: "Robert",
      role: { name: "", createdAt: "", id: "", description: "" },
      email: "",
    };
    service.refreshFromApi(mockUser);
    expect(service.userName()).toEqual("Robert");
  });

  it("should update user profile and save to localStorage", () => {
    const mockUserData = { name: "Jane", lastName: "Smith" };
    const mockResponse = { name: "Jane", lastName: "Smith" };

    service.updateUserProfile(mockUserData).subscribe((response) => {
      expect(response).toEqual(mockResponse);
      expect(localStorage.setItem).toHaveBeenCalledWith(
        "auth_user",
        JSON.stringify({ name: "Jane", lastName: "Smith" })
      );
      expect(service.userName()).toEqual("Jane");
    });

    const req = httpMock.expectOne("api/neuralforge/v1/users/profile");
    expect(req.request.method).toBe("PUT");
    req.flush(mockResponse);
  });

  it("should handle deleteAccount correctly", () => {
    service.deleteAccount().subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne("api/neuralforge/v1/users/profile");
    expect(req.request.method).toBe("DELETE");
    req.flush({});
  });
});
