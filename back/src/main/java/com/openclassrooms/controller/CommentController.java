package com.openclassrooms.controller;

import com.openclassrooms.dto.CommentDto;
import com.openclassrooms.model.Comment;
import com.openclassrooms.service.CommentService;
import com.openclassrooms.service.UserService;
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
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticleId(@PathVariable Long articleId) {
        try {
            logger.info("Récupération des commentaires pour l'article ID: {}", articleId);
            List<Comment> comments = commentService.getCommentsByArticleId(articleId);
            
            List<CommentDto> commentDtos = comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
                
            logger.info("Nombre de commentaires récupérés: {}", commentDtos.size());
            return ResponseEntity.ok(commentDtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative de création de commentaire sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour commenter");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            String content = (String) payload.get("content");
            Number articleIdNumber = (Number) payload.get("articleId");
            
            if (content == null || content.trim().isEmpty() || articleIdNumber == null) {
                logger.error("Données de commentaire invalides");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Le contenu du commentaire et l'ID de l'article sont requis");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Long articleId = articleIdNumber.longValue();
            
            logger.info("Création de commentaire: userId={}, articleId={}", userId, articleId);
            Comment savedComment = commentService.createComment(userId, articleId, content);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(CommentDto.fromEntity(savedComment));
        } catch (Exception e) {
            logger.error("Erreur lors de la création du commentaire: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative de suppression de commentaire sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour supprimer un commentaire");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Suppression du commentaire: commentId={}, userId={}", commentId, userId);
            commentService.deleteComment(commentId, userId);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du commentaire: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCommentsByCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative de récupération des commentaires sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour voir vos commentaires");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Récupération des commentaires pour l'utilisateur: {}", userId);
            List<Comment> comments = commentService.getCommentsByUserId(userId);
            
            List<CommentDto> commentDtos = comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(commentDtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des commentaires: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 