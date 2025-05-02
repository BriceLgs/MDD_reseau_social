package com.openclassrooms.service;

import com.openclassrooms.model.Article;
import com.openclassrooms.model.User;
import com.openclassrooms.model.Sujet;
import com.openclassrooms.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SujetService sujetService;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> getArticlesBySujet(Long sujetId) {
        Sujet sujet = sujetService.getSujetById(sujetId);
        return articleRepository.findBySujet(sujet);
    }

    public List<Article> getArticlesByUser(Long userId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return articleRepository.findByAuteur(user);
    }

    public Article createArticle(Article article, Long userId, Long sujetId) {
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Sujet sujet = sujetService.getSujetById(sujetId);

        article.setAuteur(user);
        article.setSujet(sujet);
        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, Article articleDetails) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        article.setTitre(articleDetails.getTitre());
        article.setContenu(articleDetails.getContenu());
        article.setStatus(articleDetails.getStatus());

        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));
        articleRepository.delete(article);
    }
} 