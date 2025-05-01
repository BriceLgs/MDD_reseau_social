package com.openclassrooms.repository;

import com.openclassrooms.model.Article;
import com.openclassrooms.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuteurId(Long auteurId);
    List<Article> findByStatus(ArticleStatus status);
    List<Article> findByAuteurIdAndStatus(Long auteurId, ArticleStatus status);
} 