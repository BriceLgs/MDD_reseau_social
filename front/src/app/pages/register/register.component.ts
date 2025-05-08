import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onSubmit() {
    this.error = '';
    
    if (this.registerForm.valid) {
      this.isLoading = true;
      const { username, email, password } = this.registerForm.value;
      
      this.authService.register(username, email, password).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/login']);
        },
        error: (err: Error) => {
          this.isLoading = false;
          console.log('Erreur d\'inscription:', err);
          this.error = err.message || 'Erreur lors de l\'inscription';
        }
      });
    } else {
      if (this.registerForm.get('username')?.hasError('required')) {
        this.error = 'Le nom d\'utilisateur est requis';
      } else if (this.registerForm.get('email')?.hasError('required')) {
        this.error = 'L\'email est requis';
      } else if (this.registerForm.get('email')?.hasError('email')) {
        this.error = 'Veuillez entrer un email valide';
      } else if (this.registerForm.get('password')?.hasError('required')) {
        this.error = 'Le mot de passe est requis';
      }
    }
  }
}