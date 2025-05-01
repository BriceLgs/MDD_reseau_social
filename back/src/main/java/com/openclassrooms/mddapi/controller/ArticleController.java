package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @GetMapping("/theme/{theme}")
    public List<Article> getArticlesByTheme(@PathVariable String theme) {
        return articleService.getArticlesByTheme(theme);
    }

    @GetMapping("/author/{authorId}")
    public List<Article> getArticlesByAuthor(@PathVariable Long authorId) {
        return articleService.getArticlesByAuthor(authorId);
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article, @RequestParam Long authorId) {
        return articleService.createArticle(article, authorId);
    }

    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return articleService.updateArticle(id, article);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }
} 