import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Article {
  id: number;
  title: string;
  content: string;
  themeId: number;
  userId: number;
  dateCreation: string;
  authorUsername?: string;
  theme?: string;
  status?: string;
}

export interface Comment {
  id: number;
  content: string;
  authorUsername: string;
  authorId: number;
  articleId: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private apiUrl = `${environment.apiUrl}/articles`;

  constructor(private http: HttpClient) { }

  getAllArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(this.apiUrl);
  }

  getArticleById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.apiUrl}/${id}`);
  }

  createArticle(article: { title: string, content: string, themeId: number }): Observable<Article> {
    console.log('Envoi de la création d\'article:', { 
      title: article.title, 
      content: article.content ? article.content.substring(0, 50) + '...' : 'vide',
      themeId: article.themeId
    });
    
    // Données simplifiées pour l'article (sans le themeId dans le corps)
    const simpleArticleData = {
      title: article.title.trim(),
      content: article.content.trim(),
      status: "DRAFT"
    };

    console.log('Données envoyées:', simpleArticleData);
    
    // Envoi du themeId uniquement dans l'URL (paramètre de requête)
    const params = new HttpParams()
      .set('themeId', article.themeId.toString());
    
    console.log('URL avec paramètres:', `${this.apiUrl}?themeId=${article.themeId}`);

    return this.http.post<Article>(this.apiUrl, simpleArticleData, { params });
  }

  updateArticle(id: number, article: Partial<Article>): Observable<Article> {
    return this.http.put<Article>(`${this.apiUrl}/${id}`, article);
  }

  deleteArticle(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getCommentsByArticleId(articleId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl.replace('/articles', '/comments/article')}/${articleId}`);
  }

  postComment(comment: { content: string; articleId: number }): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl.replace('/articles', '/comments')}`, comment);
  }
} 