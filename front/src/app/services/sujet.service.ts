import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sujet } from '../models/sujet.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SujetService {
  private apiUrl = environment.apiUrl + '/sujets';

  constructor(private http: HttpClient) {}

  getAllSujets(): Observable<Sujet[]> {
    return this.http.get<Sujet[]>(this.apiUrl);
  }

  getSujetById(id: number): Observable<Sujet> {
    return this.http.get<Sujet>(`${this.apiUrl}/${id}`);
  }

  createSujet(sujet: Sujet): Observable<Sujet> {
    return this.http.post<Sujet>(this.apiUrl, sujet);
  }

  updateSujet(id: number, sujet: Sujet): Observable<Sujet> {
    return this.http.put<Sujet>(`${this.apiUrl}/${id}`, sujet);
  }

  deleteSujet(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
} 