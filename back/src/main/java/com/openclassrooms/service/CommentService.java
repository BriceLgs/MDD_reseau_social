package com.openclassrooms.service;

import com.openclassrooms.model.Comment;
import com.openclassrooms.model.Article;
import com.openclassrooms.model.User;
import com.openclassrooms.repository.CommentRepository;
import com.openclassrooms.repository.ArticleRepository;
import com.openclassrooms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Comment> getCommentsByArticleId(Long articleId) {
        logger.info("Récupération des commentaires pour l'article ID: {}", articleId);
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
    }
    
    public Comment createComment(Long userId, Long articleId, String content) {
        logger.info("Création d'un commentaire: userId={}, articleId={}", userId, articleId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
        
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé avec ID: " + articleId));
        
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(user);
        comment.setArticle(article);
        
        Comment savedComment = commentRepository.save(comment);
        logger.info("Commentaire créé avec succès: {}", savedComment.getId());
        
        return savedComment;
    }
    
    public void deleteComment(Long commentId, Long userId) {
        logger.info("Suppression du commentaire: commentId={}, userId={}", commentId, userId);
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Commentaire non trouvé avec ID: " + commentId));
        
        
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }
        
        commentRepository.delete(comment);
        logger.info("Commentaire supprimé avec succès");
    }
    
    public List<Comment> getCommentsByUserId(Long userId) {
        logger.info("Récupération des commentaires pour l'utilisateur ID: {}", userId);
        return commentRepository.findByAuthorId(userId);
    }
} 