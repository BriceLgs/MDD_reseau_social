package com.openclassrooms.controller;

import com.openclassrooms.dto.ArticleCreateDto;
import com.openclassrooms.model.Article;
import com.openclassrooms.model.ArticleStatus;
import com.openclassrooms.model.User;
import com.openclassrooms.repository.UserRepository;
import com.openclassrooms.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "http://localhost:4200")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            return ResponseEntity.ok(articleService.getAllArticles());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<Article>> getArticlesByTheme(@PathVariable Long themeId) {
        try {
            return ResponseEntity.ok(articleService.getArticlesByTheme(themeId));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Article>> getArticlesByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(articleService.getArticlesByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestBody ArticleCreateDto articleDto,
            @RequestParam(required = false) Long themeId) {
        try {
            logger.info("=== DÉBUT REQUÊTE CRÉATION ARTICLE ===");
            
            Long finalThemeId = themeId;
            if (finalThemeId == null && articleDto.getThemeId() != null) {
                finalThemeId = articleDto.getThemeId();
                logger.info("Utilisation du themeId depuis le DTO: {}", finalThemeId);
            }
            
           
            if (finalThemeId == null) {
                logger.error("Le themeId est manquant (absent dans les paramètres et dans le DTO)");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le thème est requis pour créer un article");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative de création d'article sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour créer un article");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = articleService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Création d'article par l'utilisateur: {}", userId);
            
           
            Article article = Article.builder().title(articleDto.getTitle()).content(articleDto.getContent()).status(ArticleStatus.DRAFT).build();
            try {
                Article createdArticle = articleService.createArticle(article, userId, finalThemeId);
                return ResponseEntity.ok(createdArticle);
            } catch (Exception e) {
                logger.error("Erreur lors de la création de l'article:", e);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Erreur lors de la création: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Exception générale:", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        try {
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("Tentative de mise à jour d'article sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour modifier un article");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            
            Article existingArticle = articleService.getArticleById(id).orElse(null);
            if (existingArticle == null) {
                return ResponseEntity.notFound().build();
            }
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = articleService.getUserIdByEmail(email);
            
            if (!existingArticle.getAuthor().getId().equals(userId)) {
                logger.error("Tentative de modification d'un article par un utilisateur non autorisé");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous n'êtes pas autorisé à modifier cet article");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            Article updatedArticle = articleService.updateArticle(id, articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la mise à jour de l'article: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("Tentative de suppression d'article sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour supprimer un article");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            
            Article existingArticle = articleService.getArticleById(id).orElse(null);
            if (existingArticle == null) {
                return ResponseEntity.notFound().build();
            }
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = articleService.getUserIdByEmail(email);
            
            if (!existingArticle.getAuthor().getId().equals(userId)) {
                logger.error("Tentative de suppression d'un article par un utilisateur non autorisé");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous n'êtes pas autorisé à supprimer cet article");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            articleService.deleteArticle(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la suppression de l'article: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 