import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
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
        console.log('Tentative de connexion avec:', { email });
        
        if (!email || !password) {
            return throwError(() => new Error('Email et mot de passe requis'));
        }
        
        return this.http.post(`${environment.apiUrl}/auth/login`, { email, password })
            .pipe(
                tap(user => {
                    console.log('Connexion réussie:', user);
                    localStorage.setItem('currentUser', JSON.stringify(user));
                    this.currentUserSubject.next(user);
                }),
                catchError((error: HttpErrorResponse) => {
                    console.error('Erreur de connexion:', error);
                    
                    let errorMessage = 'Une erreur est survenue lors de la connexion';
                    
                    if (error.status === 0) {
                        errorMessage = 'Le serveur est inaccessible. Vérifiez votre connexion réseau.';
                    } else if (error.status === 400) {
                        errorMessage = error.error?.message || 'Email ou mot de passe incorrect';
                    } else if (error.status === 500) {
                        errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
                    }
                    
                    return throwError(() => new Error(errorMessage));
                })
            );
    }

    register(username: string, email: string, password: string): Observable<any> {
        console.log('Tentative d\'inscription avec:', { username, email, password: password ? '***' : undefined });
        
        if (!username) {
            console.error('Nom d\'utilisateur manquant');
            return throwError(() => new Error('Le nom d\'utilisateur est requis'));
        }
        
        if (!email) {
            console.error('Email manquant');
            return throwError(() => new Error('L\'email est requis'));
        }
        
        if (!password) {
            console.error('Mot de passe manquant');
            return throwError(() => new Error('Le mot de passe est requis'));
        }
        
        const userData = {
            username: username.trim(),
            email: email.trim(),
            password: password
        };
        
        console.log('Envoi des données:', { ...userData, password: '***' });
        
        return this.http.post(`${environment.apiUrl}/auth/register`, userData)
            .pipe(
                tap(response => {
                    console.log('Inscription réussie:', response);
                }),
                catchError((error: HttpErrorResponse) => {
                    console.error('Erreur d\'inscription:', error);
                    console.error('Statut:', error.status);
                    console.error('Message d\'erreur:', error.error);
                    
                    let errorMessage = 'Une erreur est survenue lors de l\'inscription';
                    
                    if (error.status === 0) {
                        errorMessage = 'Le serveur est inaccessible. Vérifiez votre connexion réseau.';
                    } else if (error.status === 400) {
                        if (error.error?.message) {
                            errorMessage = error.error.message;
                        } else if (error.error?.toString().includes('constraint')) {
                            errorMessage = 'Cet email ou nom d\'utilisateur existe déjà';
                        }
                    } else if (error.status === 500) {
                        errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
                    }
                    
                    return throwError(() => new Error(errorMessage));
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

    isAuthenticated(): boolean {
        return this.currentUserSubject.value !== null;
    }
} 