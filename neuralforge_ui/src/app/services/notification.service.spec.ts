import {
    HttpClientTestingModule,
    HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';
import { INotification } from '../interfaces';

describe('NotificationService', () => {
    let service: NotificationService;
    let httpMock: HttpTestingController;

    const BASE_URL = '/api/neuralforge/v1/notifications';

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [NotificationService],
        });

        service = TestBed.inject(NotificationService);
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

        Object.defineProperty(window, 'localStorage', {
            value: mockLocalStorage,
            writable: true,
        });

        // Set test user email
        localStorage.setItem(
            'auth_user',
            JSON.stringify({ email: 'test@example.com' })
        );
    });

    afterEach(() => {
        httpMock.verify();
        jest.clearAllMocks();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should get notifications by email', () => {
        const email = 'test@example.com';
        const mockNotifications: INotification[] = [/* ... */];

        service.getByEmail(email).subscribe((res) => {
            expect(res.length).toBe(1);
        });

        const req = httpMock.expectOne(`api/neuralforge/v1/notifications/email/${email}`);
        expect(req.request.method).toBe('GET');
        req.flush(mockNotifications);
    });


    it('should dismiss a notification', () => {
        const id = 'notif-001';

        service.dismissNotification(id).subscribe((res) => {
            expect(res).toBeUndefined();
        });

        const req = httpMock.expectOne(`api/neuralforge/v1/notifications/${id}/dismiss`);
        expect(req.request.method).toBe('PUT');
        req.flush(null);
    });

});
