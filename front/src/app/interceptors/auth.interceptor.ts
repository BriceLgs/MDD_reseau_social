import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Récupération du token depuis le localStorage
    const token = localStorage.getItem('currentUser') 
      ? JSON.parse(localStorage.getItem('currentUser') || '{}').token 
      : null;
    
    // Si le token existe, l'ajouter aux en-têtes de la requête
    if (token) {
      // Clone de la requête avec le token d'autorisation
      const authRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      
      // Passage de la requête modifiée au gestionnaire suivant
      return next.handle(authRequest);
    }
    
    // Si pas de token, passer la requête originale
    return next.handle(request);
  }
} 