import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { AlertService } from './alert.service';
import { IUser } from '../interfaces';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let alertService: AlertService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService, AlertService],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    alertService = TestBed.inject(AlertService);

    jest.spyOn(alertService, 'displayAlert');
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch users with parameters', () => {
    const mockResponse = { data: [{ id: "1", name: 'User Test' }], meta: { totalPages: 1 } };

    service.getAll();
    const req = httpMock.expectOne('api/neuralforge/v1/users?page=1&size=5');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);

    expect(service.users$()).toEqual(mockResponse);
  });

  it('should add a user and show success alert', () => {
    const mockUser: IUser = { id: "2", name: 'New User' };
    const mockResponse = { message: 'User added successfully' };

    service.save(mockUser);
    const req = httpMock.expectOne('api/neuralforge/v1/users');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    expect(alertService.displayAlert).toHaveBeenCalledWith('success', 'User added successfully', 'center', 'top', ['success-snackbar']);
  });

  it('should update a user and show success alert', () => {
    const mockUser: IUser = { id: "3", name: 'Updated User' };
    const mockResponse = { message: 'User updated successfully' };

    service.update(mockUser);
    const req = httpMock.expectOne(`api/neuralforge/v1/users/${mockUser.id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockResponse);

    expect(alertService.displayAlert).toHaveBeenCalledWith('success', 'User updated successfully', 'center', 'top', ['success-snackbar']);
  });

  it('should delete a user and show success alert', () => {
    const mockUser: IUser = { id: "4", name: 'Deleted User' };
    const mockResponse = { message: 'User deleted successfully' };

    service.delete(mockUser);
    const req = httpMock.expectOne(`api/neuralforge/v1/users/${mockUser.id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(mockResponse);

    expect(alertService.displayAlert).toHaveBeenCalledWith('success', 'User deleted successfully', 'center', 'top', ['success-snackbar']);
  });
});