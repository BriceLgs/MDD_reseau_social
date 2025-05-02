import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleComponent } from './pages/article/article.component';
import { MeComponent } from './pages/me/me.component';
import { SujetsComponent } from './pages/sujets/sujets.component';
import { CreateArticleComponent } from './pages/create-article/create-article.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'articles', component: ArticlesComponent },
  { path: 'articles/create', component: CreateArticleComponent },
  { path: 'article', redirectTo: 'articles', pathMatch: 'full' },
  { path: 'article/:id', component: ArticleComponent },
  { path: 'me', component: MeComponent },
  { path: 'sujets', component: SujetsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
