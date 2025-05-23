import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
// -->
export class AuthService {
    private currentUserSubject = new BehaviorSubject<any>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            this.currentUserSubject.next(JSON.parse(storedUser));
        }
    }

    login(email: string, password: string): Observable<any> {
        const loginUrl = `${environment.apiUrl}/auth/login`;
        const credentials = { email, password };
        
        return this.http.post<any>(loginUrl, credentials).pipe(
            tap(user => {
                localStorage.setItem('currentUser', JSON.stringify(user));
                this.currentUserSubject.next(user);
            }),
            catchError(error => {
                return throwError(() => error);
            })
        );
    }

    register(username: string, email: string, password: string): Observable<any> {
        const registerUrl = `${environment.apiUrl}/auth/register`;
        const userData = {
            username,
            email,
            password
        };
        
        return this.http.post<any>(registerUrl, userData).pipe(
            catchError(error => {
                return throwError(() => error);
            })
        );
    }

    logout(): void {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
    }

    getUserId(): number | null {
        const user = this.currentUserSubject.value;
        return user ? user.id : null;
    }

    getCurrentUser(): any {
        return this.currentUserSubject.value;
    }

    isAuthenticated(): boolean {
        return this.currentUserSubject.value !== null;
    }

    updateProfile(userData: { username?: string, email?: string, password?: string }): Observable<any> {
        const updateUrl = `${environment.apiUrl}/users/me`;
        
        return this.http.put<any>(updateUrl, userData).pipe(
            tap(updatedUser => {
                const currentUser = this.getCurrentUser();
                if (currentUser) {
                    const updatedUserData = { ...currentUser, ...updatedUser };
                    localStorage.setItem('currentUser', JSON.stringify(updatedUserData));
                    this.currentUserSubject.next(updatedUserData);
                }
            }),
            catchError(error => {
                return throwError(() => error);
            })
        );
    }
} 