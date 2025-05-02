import { Component, OnInit } from '@angular/core';
import { SujetService } from 'src/app/services/sujet.service';
import { SubscriptionService } from '../../services/subscription.service';
import { AuthService } from '../../services/auth.service';
import { Sujet } from 'src/app/models/sujet.model';

@Component({
  selector: 'app-sujets',
  templateUrl: './sujets.component.html',
  styleUrls: ['./sujets.component.css']
})
export class SujetsComponent implements OnInit {
  sujets: Sujet[] = [];
  abonnements: number[] = [];
  messageErreur = '';
  nouveauSujet = {
    name: '',
    description: '',
    dateCreation: ''
  };

  constructor(
    private sujetService: SujetService,
    private subscriptionService: SubscriptionService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.chargerSujets();
    this.chargerAbonnements();
  }

  chargerSujets() {
    this.sujetService.getAllSujets().subscribe({
      next: (data) => this.sujets = data,
      error: () => this.messageErreur = 'Erreur de chargement'
    });
  }

  chargerAbonnements() {
    const id = this.authService.getUserId();
    if (!id) return;

    this.subscriptionService.getUserSubscriptions(id).subscribe({
      next: (data) => {
        this.abonnements = [];
        data.forEach(subscription => {
          if (subscription.sujet && subscription.sujet.id) {
            this.abonnements.push(subscription.sujet.id);
          }
        });
      },
      error: () => this.messageErreur = 'Erreur de chargement'
    });
  }

  ajouterSujet() {
    if (!this.nouveauSujet.name || !this.nouveauSujet.description) {
      this.messageErreur = 'Remplir tous les champs';
      return;
    }

    this.sujetService.createSujet(this.nouveauSujet).subscribe({
      next: (sujet) => {
        this.sujets.push(sujet);
        this.nouveauSujet.name = '';
        this.nouveauSujet.description = '';
        this.messageErreur = '';
      },
      error: () => this.messageErreur = 'Erreur creation theme'
    });
  }

  estAbonne(id: number) {
    return this.abonnements.includes(id);
  }

  gererAbonnement(id: number) {
    const userId = this.authService.getUserId();
    if (!userId) return;

    if (this.estAbonne(id)) {
      this.subscriptionService.unsubscribeFromSujet(userId, id).subscribe({
        next: () => {
          this.abonnements = this.abonnements.filter(a => a !== id);
        },
        error: () => this.messageErreur = 'Erreur'
      });
    } else {
      this.subscriptionService.subscribeToSujet(userId, id).subscribe({
        next: () => {
          this.abonnements.push(id);
        },
        error: () => this.messageErreur = 'Erreur'
      });
    }
  }
} 