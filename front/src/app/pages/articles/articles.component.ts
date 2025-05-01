import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface Article {
  id: string;
  title: string;
  date: Date;
  author: string;
  content: string;
}

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss']
})
export class ArticlesComponent implements OnInit {
  articles: Article[] = [
    {
      id: '1',
      title: 'Titre de l\'article',
      date: new Date(),
      author: 'Auteur',
      content: 'Content: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled...'
    },
    {
      id: '2',
      title: 'Titre de l\'article',
      date: new Date(),
      author: 'Auteur',
      content: 'Content: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled...'
    },
    {
      id: '3',
      title: 'Titre de l\'article',
      date: new Date(),
      author: 'Auteur',
      content: 'Content: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled...'
    },
    {
      id: '4',
      title: 'Titre de l\'article',
      date: new Date(),
      author: 'Auteur',
      content: 'Content: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled...'
    }
  ];

  sortAscending = true;
  isMobileMenuOpen = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Ici, nous ajouterons plus tard la logique pour charger les articles depuis l'API
  }

  logout(): void {
    // Ajoutez ici la logique de déconnexion
    localStorage.removeItem('token');
    this.router.navigate(['/']);
  }

  toggleSort(): void {
    this.sortAscending = !this.sortAscending;
    this.articles.sort((a, b) => {
      const comparison = a.date.getTime() - b.date.getTime();
      return this.sortAscending ? comparison : -comparison;
    });
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  navigateToArticle(id: string): void {
    this.router.navigate(['/article', id]);
  }
} 