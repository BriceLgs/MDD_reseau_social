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

  // Récupère les abonnements de l'utilisateur courant
  getUserSubscriptions(): Observable<Subscription[]> {
    console.log('Récupération des abonnements...');
    return this.http.get<Subscription[]>(`${this.apiUrl}/user`)
      .pipe(
        tap(subscriptions => {
          console.log('Abonnements récupérés:', subscriptions);
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des abonnements:', error);
          throw error;
        })
      );
  }

  // Récupère les abonnements d'un utilisateur spécifique (admin ou profil public)
  getSubscriptionsByUserId(userId: number): Observable<Subscription[]> {
    console.log(`Récupération des abonnements pour l'utilisateur ${userId}...`);
    return this.http.get<Subscription[]>(`${this.apiUrl}/user/${userId}`)
      .pipe(
        tap(subscriptions => {
          console.log(`Abonnements récupérés pour l'utilisateur ${userId}:`, subscriptions);
        }),
        catchError(error => {
          console.error(`Erreur lors de la récupération des abonnements pour l'utilisateur ${userId}:`, error);
          throw error;
        })
      );
  }

  // S'abonner à un thème
  subscribeToTheme(themeId: number): Observable<Subscription> {
    console.log(`Tentative d'abonnement au thème ${themeId}...`);
    const params = new HttpParams().set('themeId', themeId.toString());
    return this.http.post<Subscription>(`${this.apiUrl}/subscribe`, null, { params })
      .pipe(
        tap(subscription => {
          console.log(`Abonnement réussi au thème ${themeId}:`, subscription);
        }),
        catchError(error => {
          console.error(`Erreur lors de l'abonnement au thème ${themeId}:`, error);
          throw error;
        })
      );
  }

  // Se désabonner d'un thème
  unsubscribeFromTheme(themeId: number): Observable<void> {
    console.log(`Tentative de désabonnement du thème ${themeId}...`);
    const params = new HttpParams().set('themeId', themeId.toString());
    return this.http.delete<void>(`${this.apiUrl}/unsubscribe`, { params })
      .pipe(
        tap(() => {
          console.log(`Désabonnement réussi du thème ${themeId}`);
        }),
        catchError(error => {
          console.error(`Erreur lors du désabonnement du thème ${themeId}:`, error);
          throw error;
        })
      );
  }

  // Méthode de débogage pour récupérer tous les abonnements
  getAllSubscriptions(): Observable<Subscription[]> {
    console.log('Récupération de tous les abonnements (débogage)...');
    return this.http.get<Subscription[]>(`${this.apiUrl}/debug/all`)
      .pipe(
        tap(subscriptions => {
          console.log('Tous les abonnements récupérés:', subscriptions);
          console.table(subscriptions);
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de tous les abonnements:', error);
          throw error;
        })
      );
  }

  // Méthode de débogage pour vérifier la structure de la table subscriptions
  checkSubscriptionStructure(): Observable<any> {
    console.log('Vérification de la structure de la table subscriptions...');
    return this.http.get<any>(`${this.apiUrl}/debug/structure`)
      .pipe(
        tap(result => {
          console.log('Structure de la table:', result);
          console.log('Colonnes:');
          console.table(result.columns);
          
          if (result.sampleData && result.sampleData.length > 0) {
            console.log('Échantillon de données:');
            console.table(result.sampleData);
          } else {
            console.log('Aucun échantillon de données disponible');
          }
        }),
        catchError(error => {
          console.error('Erreur lors de la vérification de la structure:', error);
          throw error;
        })
      );
  }
} 