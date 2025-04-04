import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { INotification } from '../interfaces';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private http = inject(HttpClient);
    private baseUrl = 'api/neuralforge/v1/notifications';

    /**
     * Get notifications by user email
     */
    getByEmail(email: string): Observable<INotification[]> {
        return this.http.get<INotification[]>(`${this.baseUrl}/email/${email}`);
    }

    /**
     * Dismiss a notification by ID
     */
    dismissNotification(notificationId: string): Observable<void> {
        return this.http.put<void>(`${this.baseUrl}/${notificationId}/dismiss`, {});
    }
}
