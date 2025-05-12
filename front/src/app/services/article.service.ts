import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, forkJoin, of, map, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { SubscriptionService } from './subscription.service';

export interface Article {
  id: number;
  title: string;
  content: string;
  themeId: number;
  userId: number;
  dateCreation: string;
  authorUsername?: string;
  themeName?: string;
  theme?: {
    id: number;
    name: string;
  };
  author?: {
    id: number;
    username: string;
    email: string;
  };
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

  constructor(
    private http: HttpClient,
    private subscriptionService: SubscriptionService
  ) { }

  getAllArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(this.apiUrl);
  }

  getSubscribedArticles(): Observable<Article[]> {
    return this.subscriptionService.getUserSubscriptions().pipe(
      switchMap(subscriptions => {
        if (!subscriptions || subscriptions.length === 0) {
          return of([]);
        }

        const themeIds = subscriptions.map(sub => sub.themeId);

        return this.getAllArticles().pipe(
          map(articles => {
            const filteredArticles = articles.filter(article => {
              const articleThemeId = article.theme?.id || article.themeId;
              return articleThemeId && themeIds.includes(articleThemeId);
            });
            
            return filteredArticles;
          })
        );
      })
    );
  }

  getArticleById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.apiUrl}/${id}`);
  }

  createArticle(article: { title: string, content: string, themeId: number }): Observable<Article> {
    const simpleArticleData = {
      title: article.title.trim(),
      content: article.content.trim(),
      status: "DRAFT"
    };
    
    const params = new HttpParams()
      .set('themeId', article.themeId.toString());

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