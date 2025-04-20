import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {IVirtualClass} from "../interfaces";

@Injectable({
    providedIn: 'root',
})
export class VirtualClassService {

    private apiUrl: string = `api/neuralforge/v1/virtual-classes`; // Replace with your API's base URL

    constructor(private http: HttpClient) {}

    // Get all virtual classes
    public getAll(): Observable<IVirtualClass[]> {
        return this.http.get<IVirtualClass[]>(this.apiUrl).pipe(
            map((response) => response), // Adjust as needed based on the actual response structure
            catchError(this.handleError)
        );
    }

    // Get a specific virtual class by ID
    public getById(id: string): Observable<IVirtualClass> {
        return this.http.get<IVirtualClass>(`${this.apiUrl}/${id}`).pipe(
            catchError(this.handleError)
        );
    }

    // Create a new virtual class
    public create(virtualClass: IVirtualClass): Observable<IVirtualClass> {
        return this.http.post<IVirtualClass>(this.apiUrl, virtualClass).pipe(
            catchError(this.handleError)
        );
    }

    // Update an existing virtual class
    public update(id: string, virtualClass: IVirtualClass): Observable<IVirtualClass> {
        return this.http.put<IVirtualClass>(`${this.apiUrl}/${id}`, virtualClass).pipe(
            catchError(this.handleError)
        );
    }

    // Delete a virtual class by ID
    public delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
            catchError(this.handleError)
        );
    }

    // Add a student to a virtual class
    public addStudentToClass(classId: string, studentId: string): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${classId}/${studentId}`, null).pipe(
            catchError(this.handleError)
        );
    }

    public getMine(): Observable<IVirtualClass[]> {
        return this.http.get<IVirtualClass[]>(`${this.apiUrl}/my`).pipe(
            catchError(this.handleError)
        );
    }


    // Handle HTTP errors
    private handleError(error: any): Observable<never> {
        console.error('Error occurred:', error);
        throw error; // You can add more custom error handling logic here
    }

}
