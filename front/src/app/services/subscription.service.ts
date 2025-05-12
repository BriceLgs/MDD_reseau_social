import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface Subscription {
  id: number;
  themeName: string;
  themeId: number;
  userId: number;
  dateSubscription: string;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = `${environment.apiUrl}/subscriptions`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  getUserSubscriptions(): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/user`)
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }

  getSubscriptionsByUserId(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/user/${userId}`)
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }

  subscribeToTheme(themeId: number): Observable<Subscription> {
    const params = new HttpParams().set('themeId', themeId.toString());
    return this.http.post<Subscription>(`${this.apiUrl}/subscribe`, null, { params })
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }

  unsubscribeFromTheme(themeId: number): Observable<void> {
    const params = new HttpParams().set('themeId', themeId.toString());
    return this.http.delete<void>(`${this.apiUrl}/unsubscribe`, { params })
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }

  getAllSubscriptions(): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/debug/all`)
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }

  checkSubscriptionStructure(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/debug/structure`)
      .pipe(
        catchError(error => {
          throw error;
        })
      );
  }
} 