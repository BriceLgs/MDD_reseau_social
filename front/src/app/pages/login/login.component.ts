import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {}

  onSubmit() {
    this.error = '';
    
    if (this.loginForm.valid) {
      this.isLoading = true;
      const { email, password } = this.loginForm.value;
      
      console.log('Soumission du formulaire:', { email, password: '***' });
      
      this.authService.login(email, password).subscribe({
        next: () => {
          console.log('Redirection vers /articles');
          this.router.navigate(['/articles']);
        },
        error: (err: HttpErrorResponse) => {
          console.error('Erreur complète:', err);
          if (err.status === 0) {
            this.error = 'Impossible de contacter le serveur. Veuillez vérifier votre connexion.';
          } else if (err.status === 400) {
            this.error = err.error?.message || 'Email ou mot de passe incorrect';
          } else {
            this.error = 'Une erreur est survenue. Veuillez réessayer plus tard.';
          }
          this.isLoading = false;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    } else {
      if (this.loginForm.get('email')?.hasError('required')) {
        this.error = 'L\'email est requis';
      } else if (this.loginForm.get('email')?.hasError('email')) {
        this.error = 'Veuillez entrer un email valide';
      } else if (this.loginForm.get('password')?.hasError('required')) {
        this.error = 'Le mot de passe est requis';
      }
    }
  }
}
