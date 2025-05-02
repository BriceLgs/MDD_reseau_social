import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SubjectService } from '../../services/subject.service';
import { ArticleService } from 'src/app/services/article.service';
import { Subject } from '../../models/subject.model';

@Component({
  selector: 'app-create-article',
  templateUrl: './create-article.component.html',
  styleUrls: ['./create-article.component.css']
})
export class CreateArticleComponent implements OnInit {
  articleForm: FormGroup;
  subjects: Subject[] = [];
  error: string = '';

  constructor(
    private fb: FormBuilder,
    private subjectService: SubjectService,
    private articleService: ArticleService,
    private router: Router
  ) {
    this.articleForm = this.fb.group({
      subjectId: ['', Validators.required],
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadSubjects();
  }

  loadSubjects(): void {
    this.subjectService.getAllSubjects().subscribe({
      next: (subjects) => {
        this.subjects = subjects;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.error = 'Erreur lors du chargement des thèmes';
      }
    });
  }

  onSubmit(): void {
    if (this.articleForm.valid) {
      this.articleService.createArticle(this.articleForm.value).subscribe({
        next: () => {
          this.router.navigate(['/articles']);
        },
        error: (error) => {
          console.error('Erreur lors de la création de l\'article:', error);
          this.error = 'Erreur lors de la création de l\'article';
        }
      });
    }
  }
} 