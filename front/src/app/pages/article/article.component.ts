import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

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
  article: Article = {
    id: '1',
    title: 'Titre de l\'article sélectionné',
    content: 'Content: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.',
    author: 'Auteur',
    date: new Date(),
    theme: 'Thème',
    comments: [
      {
        username: 'username',
        content: 'contenu du commentaire',
        date: new Date()
      }
    ]
  };

  newComment: string = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const articleId = this.route.snapshot.paramMap.get('id');
  }

  goBack(): void {
    this.router.navigate(['/articles']);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/']);
  }

  submitComment(): void {
    if (this.newComment.trim()) {
      this.article.comments.push({
        username: 'Utilisateur a remplacer',
        content: this.newComment,
        date: new Date()
      });
      this.newComment = '';
    }
  }
} 