import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { SubscriptionService, Subscription } from '../../services/subscription.service';
import { Router } from '@angular/router';
import { catchError, finalize, of } from 'rxjs';

@Component({
  selector: 'app-me',
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss']
})
export class MeComponent implements OnInit {
  user: any = null;
  subscriptions: Subscription[] = [];
  loading: boolean = true;
  error: string = '';
  debugInfo: string = '';
  showingDebugInfo: boolean = false;

  constructor(
    private authService: AuthService,
    private subscriptionService: SubscriptionService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log('Initialisation du composant Me');
    // Vérifier si l'utilisateur est connecté
    if (!this.authService.isAuthenticated()) {
      console.log('Utilisateur non authentifié, redirection vers login');
      this.router.navigate(['/login']);
      return;
    }

    // Récupérer les informations de l'utilisateur
    this.authService.currentUser$.subscribe(
      user => {
        console.log('Utilisateur récupéré:', user);
        this.user = user;
        if (user && user.id) {
          console.log('ID de l\'utilisateur:', user.id);
          // Alternative: récupérer les abonnements avec l'ID direct
          this.loadSubscriptionsByUserId(user.id);
        } else {
          this.loadSubscriptions();
        }
      },
      error => {
        console.error('Erreur lors de la récupération de l\'utilisateur:', error);
        this.error = "Erreur lors de la récupération des informations utilisateur";
        this.loading = false;
      }
    );
  }

  loadSubscriptions(): void {
    console.log('Chargement des abonnements via /user...');
    this.subscriptionService.getUserSubscriptions()
      .pipe(
        catchError(error => {
          console.error('Erreur interceptée:', error);
          this.error = "Erreur lors de la récupération des abonnements: " + (error.message || 'Erreur inconnue');
          this.debugInfo = JSON.stringify(error);
          return of([]);
        }),
        finalize(() => {
          this.loading = false;
          console.log('Chargement terminé, abonnements:', this.subscriptions);
        })
      )
      .subscribe(
        subscriptions => {
          console.log('Abonnements reçus:', subscriptions);
          this.subscriptions = subscriptions;
          // Si aucun abonnement n'est récupéré, essayons la méthode de débogage
          if (subscriptions.length === 0) {
            this.loadAllSubscriptionsDebug();
          }
        }
      );
  }

  loadSubscriptionsByUserId(userId: number): void {
    console.log(`Chargement des abonnements via /user/${userId}...`);
    this.subscriptionService.getSubscriptionsByUserId(userId)
      .pipe(
        catchError(error => {
          console.error('Erreur interceptée:', error);
          this.error = "Erreur lors de la récupération des abonnements: " + (error.message || 'Erreur inconnue');
          this.debugInfo = JSON.stringify(error);
          return of([]);
        }),
        finalize(() => {
          this.loading = false;
          console.log('Chargement terminé, abonnements:', this.subscriptions);
        })
      )
      .subscribe(
        subscriptions => {
          console.log('Abonnements reçus:', subscriptions);
          this.subscriptions = subscriptions;
          // Si aucun abonnement n'est récupéré, essayons la méthode de débogage
          if (subscriptions.length === 0) {
            this.loadAllSubscriptionsDebug();
          }
        }
      );
  }

  // Méthode de débogage pour récupérer tous les abonnements
  loadAllSubscriptionsDebug(): void {
    console.log('Tentative de récupération de tous les abonnements (débogage)...');
    this.loading = true;
    this.showingDebugInfo = true;
    this.debugInfo = "Démarrage du débogage...";
    
    // D'abord, vérifier la structure
    this.subscriptionService.checkSubscriptionStructure()
      .pipe(
        catchError(error => {
          this.debugInfo += "\nErreur lors de la vérification de structure: " + JSON.stringify(error);
          console.error('Erreur lors de la vérification de structure:', error);
          return of(null);
        })
      )
      .subscribe(structure => {
        if (structure) {
          this.debugInfo += "\nStructure vérifiée, colonnes: " + structure.columns.length;
          
          if (structure.sampleData && structure.sampleData.length > 0) {
            this.debugInfo += "\nDonnées trouvées: " + structure.sampleData.length + " enregistrements";
          } else {
            this.debugInfo += "\nAucune donnée trouvée dans la table subscriptions";
          }
        }
        
        // Ensuite, récupérer tous les abonnements
        this.subscriptionService.getAllSubscriptions()
          .pipe(
            catchError(error => {
              console.error('Erreur lors du débogage:', error);
              this.debugInfo += "\nErreur débogage getAllSubscriptions: " + JSON.stringify(error);
              return of([]);
            }),
            finalize(() => {
              this.loading = false;
            })
          )
          .subscribe(allSubscriptions => {
            console.log('Tous les abonnements récupérés (débogage):', allSubscriptions);
            this.debugInfo += "\nNombre total d'abonnements: " + allSubscriptions.length;
            
            if (this.user && this.user.id) {
              // Filtrer pour trouver les abonnements de l'utilisateur actuel
              const userSubs = allSubscriptions.filter(sub => sub.userId === this.user.id);
              console.log(`Abonnements filtrés pour l'utilisateur ${this.user.id}:`, userSubs);
              
              if (userSubs.length > 0) {
                this.subscriptions = userSubs;
                this.debugInfo += "\nAbonnements trouvés par la méthode debug: " + userSubs.length;
              } else {
                this.debugInfo += "\nAucun abonnement trouvé pour l'utilisateur " + this.user.id;
              }
            }
          });
      });
  }

  unsubscribe(themeId: number): void {
    console.log(`Désabonnement du thème ${themeId}...`);
    this.loading = true;
    this.subscriptionService.unsubscribeFromTheme(themeId)
      .pipe(
        catchError(error => {
          console.error('Erreur lors du désabonnement:', error);
          this.error = "Erreur lors du désabonnement: " + (error.message || 'Erreur inconnue');
          return of(void 0);
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(
        () => {
          console.log('Désabonnement réussi, mise à jour de la liste');
          this.subscriptions = this.subscriptions.filter(sub => sub.themeId !== themeId);
        }
      );
  }
}
