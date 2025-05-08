import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ArticleService, Comment as ApiComment } from 'src/app/services/article.service';
import { AuthService } from 'src/app/services/auth.service';

interface Comment {
  username: string;
  content: string;
  date: Date;
}

interface Article {
  id: string;
  title: string;
  content: string;
  author: string;
  date: Date;
  theme: string;
  comments: Comment[];
}

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.scss']
})
export class ArticleComponent implements OnInit {
  article: any = null;
  comments: ApiComment[] = [];
  newComment: string = '';
  articleId: number = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private articleService: ArticleService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.articleId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadArticle();
    this.loadComments();
  }

  loadArticle(): void {
    this.articleService.getArticleById(this.articleId).subscribe(article => {
      this.article = article;
    });
  }

  loadComments(): void {
    this.articleService.getCommentsByArticleId(this.articleId).subscribe(comments => {
      this.comments = comments;
    });
  }

  goBack(): void {
    this.router.navigate(['/articles']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  submitComment(): void {
    if (this.newComment.trim()) {
      this.articleService.postComment({
        content: this.newComment,
        articleId: this.articleId
      }).subscribe(() => {
        this.newComment = '';
        this.loadComments();
      });
    }
  }
} 