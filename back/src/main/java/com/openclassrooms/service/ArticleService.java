package com.openclassrooms.service;

import com.openclassrooms.model.Article;
import com.openclassrooms.model.ArticleStatus;
import com.openclassrooms.model.Theme;
import com.openclassrooms.model.User;
import com.openclassrooms.repository.ArticleRepository;
import com.openclassrooms.repository.UserRepository;
import com.openclassrooms.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ThemeService themeService;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> getArticlesByTheme(Long themeId) {
        Theme theme = themeService.getThemeById(themeId);
        return articleRepository.findByTheme(theme);
    }

    public List<Article> getArticlesByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return articleRepository.findByAuthor(user);
    }

    @Transactional
    public Article createArticle(Article article, Long userId, Long themeId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
            Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Thème non trouvé avec ID: " + themeId));
            

            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                throw new RuntimeException("Le titre de l'article ne peut pas être vide");
            }
            
            if (article.getContent() == null || article.getContent().trim().isEmpty()) {
                throw new RuntimeException("Le contenu de l'article ne peut pas être vide");
            }
            
            
            Article newArticle = new Article();
             newArticle.setTitle(article.getTitle().trim());
            newArticle.setContent(article.getContent().trim());
            newArticle.setAuthor(user);
            newArticle.setTheme(theme);
        
            newArticle.setThemeName(theme.getName());
            
            newArticle.setDateCreation(LocalDateTime.now());
            newArticle.setStatus(article.getStatus() != null ? article.getStatus() : ArticleStatus.DRAFT);

            return articleRepository.save(newArticle);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'article:", e);
            throw new RuntimeException("Erreur lors de la création de l'article: " + e.getMessage());
        }
    }

    @Transactional
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID: " + id));
        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé avec l'ID: " + id));
        articleRepository.delete(article);
    }
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }
} 