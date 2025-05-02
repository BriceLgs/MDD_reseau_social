package com.openclassrooms.controller;

import com.openclassrooms.model.Article;
import com.openclassrooms.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/subject/{subjectId}")
    public List<Article> getArticlesBySubject(@PathVariable Long subjectId) {
        return articleService.getArticlesBySujet(subjectId);
    }

    @GetMapping("/user/{userId}")
    public List<Article> getArticlesByUser(@PathVariable Long userId) {
        return articleService.getArticlesByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(
            @RequestBody Article article,
            @RequestParam Long userId,
            @RequestParam Long subjectId) {
        try {
            Article createdArticle = articleService.createArticle(article, userId, subjectId);
            return ResponseEntity.ok(createdArticle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        try {
            Article updatedArticle = articleService.updateArticle(id, articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 