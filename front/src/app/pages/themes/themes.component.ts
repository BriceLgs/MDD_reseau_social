import { Component, OnInit } from '@angular/core';
import { ThemeService } from 'src/app/services/theme.service';
import { SubscriptionService, Subscription } from 'src/app/services/subscription.service';
import { Theme } from 'src/app/models/theme.model';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-themes',
  templateUrl: './themes.component.html',
  styleUrls: ['./themes.component.css']
})
export class ThemesComponent implements OnInit {
  themes: Theme[] = [];
  abonnements: number[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private themeService: ThemeService,
    private subscriptionService: SubscriptionService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.chargerThemes();
    this.chargerAbonnements();
  }

  chargerThemes() {
    this.loading = true;
    this.themeService.getAllThemes().subscribe({
      next: (data) => {
        this.themes = data;
        this.loading = false;
      },
      error: (error: Error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.error = 'Impossible de charger les thèmes';
        this.loading = false;
      }
    });
  }

  chargerAbonnements() {
    this.subscriptionService.getUserSubscriptions().subscribe({
      next: (subscriptions) => {
        this.abonnements = [];
        subscriptions.forEach(subscription => {
          if (subscription.themeId) {
            this.abonnements.push(subscription.themeId);
          }
        });
      },
      error: (error: Error) => {
        console.error('Erreur lors du chargement des abonnements:', error);
        this.snackBar.open('Erreur lors du chargement des abonnements', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  estAbonne(id: number): boolean {
    return this.abonnements.includes(id);
  }

  gererAbonnement(id: number) {
    if (this.estAbonne(id)) {
      this.subscriptionService.unsubscribeFromTheme(id).subscribe({
        next: () => {
          this.abonnements = this.abonnements.filter(themeId => themeId !== id);
          this.snackBar.open('Désabonnement réussi', 'Fermer', {
            duration: 3000
          });
        },
        error: (error: HttpErrorResponse) => {
          console.error('Erreur lors du désabonnement:', error);
          let message = 'Erreur lors du désabonnement';
          
          if (error.error && error.error.error) {
            message = error.error.error;
          }
          
          this.snackBar.open(message, 'Fermer', {
            duration: 5000
          });
        }
      });
    } else {
      this.subscriptionService.subscribeToTheme(id).subscribe({
        next: () => {
          this.abonnements.push(id);
          this.snackBar.open('Abonnement réussi', 'Fermer', {
            duration: 3000
          });
        },
        error: (error: HttpErrorResponse) => {
          console.error('Erreur lors de l\'abonnement:', error);
          let message = 'Erreur lors de l\'abonnement';
          
          if (error.error && error.error.error) {
            message = error.error.error;
          }
          
          this.snackBar.open(message, 'Fermer', {
            duration: 5000
          });
        }
      });
    }
  }
} 