import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BaseService } from './base-service';
import { IResponse } from '../interfaces';

interface MockEntity {
  id: number;
  name: string;
}

describe('BaseService', () => {
  let service: BaseService<MockEntity>;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BaseService]
    });

    service = TestBed.inject(BaseService);
    httpMock = TestBed.inject(HttpTestingController);
    (service as any).source = 'api/mock'; // Seteamos el endpoint de prueba
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve an entity by ID', () => {
    const mockResponse: Partial<IResponse<MockEntity>> = {
      data: { id: 1, name: 'Test Entity' },
    };

    service.find(1).subscribe(response => {
      expect(response.data).toEqual(mockResponse.data);
    });

    const req = httpMock.expectOne('api/mock/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should retrieve all entities', () => {
    const mockResponse: Partial<IResponse<MockEntity[]>> = {
      data: [
        { id: 1, name: 'Entity 1' },
        { id: 2, name: 'Entity 2' }
      ]
    };

    service.findAll().subscribe(response => {
      expect(response.data).toEqual(mockResponse.data);
    });

    const req = httpMock.expectOne('api/mock');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should add a new entity', () => {
    const newEntity = { name: 'New Entity' };
    const mockResponse: Partial<IResponse<MockEntity>> = {
      data: { id: 3, name: 'New Entity' },
    };

    service.add(newEntity).subscribe(response => {
      expect(response.data).toEqual(mockResponse.data);
    });

    const req = httpMock.expectOne('api/mock');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should update an entity', () => {
    const updatedEntity = { name: 'Updated Entity' };
    const mockResponse: Partial<IResponse<MockEntity>> = {
      data: { id: 1, name: 'Updated Entity' },
    };

    service.edit(1, updatedEntity).subscribe(response => {
      expect(response.data).toEqual(mockResponse.data);
    });

    const req = httpMock.expectOne('api/mock/1');
    expect(req.request.method).toBe('PUT');
    req.flush(mockResponse);
  });

  it('should delete an entity', () => {
    const mockResponse: Partial<IResponse<MockEntity>> = {};

    service.del(1).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/mock/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(mockResponse);
  });
});