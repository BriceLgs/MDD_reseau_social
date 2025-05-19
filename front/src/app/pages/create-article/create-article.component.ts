import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ThemeService } from '../../services/theme.service';
import { ArticleService } from 'src/app/services/article.service';
import { Theme } from '../../models/theme.model';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-create-article',
  templateUrl: './create-article.component.html',
  styleUrls: ['./create-article.component.scss']
})
export class CreateArticleComponent implements OnInit {
  articleForm: FormGroup;
  themes: Theme[] = [];
  error: string = '';
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private themeService: ThemeService,
    private articleService: ArticleService,
    private authService: AuthService,
    private router: Router
  ) {
    this.articleForm = this.fb.group({
      themeId: ['', Validators.required],
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    this.loadThemes();
    if (!this.authService.isAuthenticated()) {
      console.warn('Utilisateur non authentifié, redirection vers la page de connexion');
      this.router.navigate(['/login']);
    }
  }

  loadThemes(): void {
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes = themes;
        console.log('Thèmes chargés:', themes);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.error = 'Erreur lors du chargement des thèmes';
      }
    });
  }

  onSubmit(): void {
    if (this.articleForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.error = '';
      const formData = this.articleForm.value;
      
      console.log('Soumission du formulaire de création d\'article:', {
        themeId: formData.themeId,
        title: formData.title,
        contentLength: formData.content ? formData.content.length : 0
      });
      
      if (!this.authService.isAuthenticated()) {
        console.error('Tentative de création d\'article sans être authentifié');
        this.error = 'Vous devez être connecté pour créer un article';
        this.isSubmitting = false;
        return;
      }
      
      if (formData.title.trim().length < 3) {
        this.error = 'Le titre doit contenir au moins 3 caractères';
        this.isSubmitting = false;
        return;
      }
      
      if (formData.content.trim().length < 10) {
        this.error = 'Le contenu doit contenir au moins 10 caractères';
        this.isSubmitting = false;
        return;
      }
      
      this.articleService.createArticle(formData).subscribe({
        next: (article) => {
          console.log('Article créé avec succès:', article);
          this.isSubmitting = false;
          this.router.navigate(['/articles']);
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting = false;
          console.error('Erreur lors de la création de l\'article:', error);
          
          if (error.status === 401) {
            console.error('Erreur d\'authentification, redirection vers login');
            this.error = 'Vous devez être connecté pour créer un article';
            this.router.navigate(['/login']);
            return;
          }
          
          if (error.error && typeof error.error === 'object') {
            if (error.error.error) {
              this.error = `Erreur: ${error.error.error}`;
            } else if (error.error.message) {
              this.error = `Erreur: ${error.error.message}`;
            } else if (error.error.details) {
              this.error = `Erreur: ${error.error.details}`;
            } else {
              this.error = 'Erreur lors de la création de l\'article';
            }
          } else {
            this.error = 'Erreur lors de la création de l\'article';
          }
        }
      });
    } else {
      if (this.articleForm.get('themeId')?.hasError('required')) {
        this.error = 'Veuillez sélectionner un thème';
      } else if (this.articleForm.get('title')?.hasError('required')) {
        this.error = 'Le titre est requis';
      } else if (this.articleForm.get('title')?.hasError('minlength')) {
        this.error = 'Le titre doit contenir au moins 3 caractères';
      } else if (this.articleForm.get('content')?.hasError('required')) {
        this.error = 'Le contenu est requis';
      } else if (this.articleForm.get('content')?.hasError('minlength')) {
        this.error = 'Le contenu doit contenir au moins 10 caractères';
      }
    }
  }
} 