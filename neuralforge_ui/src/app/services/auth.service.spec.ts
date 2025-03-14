import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return false when user is not authenticated', () => {
    expect(service.check()).toBe(false);
  });

  it('should perform login and return token', () => {
    const mockResponse = { accessToken: 'sample-token', expiresIn: 3600 };

    service.login({ email: 'test@example.com', password: '123456' }).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/neuralforge/v1/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});