import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sujet } from '../models/sujet.model';
import { environment } from '../../environments/environment';

export interface Subscription {
  id: number;
  user: any;
  sujet: Sujet;
  dateCreation: string;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = `${environment.apiUrl}/subscriptions`;

  constructor(private http: HttpClient) { }

  getUserSubscriptions(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/user/${userId}`);
  }

  subscribeToSujet(userId: number, sujetId: number): Observable<Subscription> {
    return this.http.post<Subscription>(`${this.apiUrl}/subscribe`, null, {
      params: {
        userId: userId.toString(),
        sujetId: sujetId.toString()
      }
    });
  }

  unsubscribeFromSujet(userId: number, sujetId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/unsubscribe`, {
      params: {
        userId: userId.toString(),
        sujetId: sujetId.toString()
      }
    });
  }
} 