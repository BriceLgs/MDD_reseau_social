import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ArticleService, Article } from 'src/app/services/article.service';
import { AuthService } from 'src/app/services/auth.service';
import { SubscriptionService } from 'src/app/services/subscription.service';

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss']
})
export class ArticlesComponent implements OnInit {
  articles: Article[] = [];
  sortAscending = true;
  isMobileMenuOpen = false;

  constructor(
    private router: Router, 
    private articleService: ArticleService,
    private authService: AuthService,
    private subscriptionService: SubscriptionService
  ) {}

  ngOnInit(): void {
    this.loadArticles();
  }

  loadArticles(): void {
    this.articleService.getSubscribedArticles().subscribe(articles => {
      this.articles = articles;
      this.sortArticles();
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  toggleSort(): void {
    this.sortAscending = !this.sortAscending;
    this.sortArticles();
  }

  sortArticles(): void {
    this.articles.sort((a, b) => {
      const comparison = new Date(a.dateCreation).getTime() - new Date(b.dateCreation).getTime();
      return this.sortAscending ? comparison : -comparison;
    });
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  navigateToArticle(id: number): void {
    this.router.navigate(['/article', id]);
  }
} 