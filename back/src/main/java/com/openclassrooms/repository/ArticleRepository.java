package com.openclassrooms.repository;

import com.openclassrooms.model.Article;
import com.openclassrooms.model.ArticleStatus;
import com.openclassrooms.model.User;
import com.openclassrooms.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTheme(Theme theme);
    List<Article> findByAuthor(User author);
    List<Article> findByAuthorAndStatus(User author, ArticleStatus status);
} 