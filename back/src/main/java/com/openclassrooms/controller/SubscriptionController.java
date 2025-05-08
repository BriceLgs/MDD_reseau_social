package com.openclassrooms.controller;

import com.openclassrooms.dto.SubscriptionDto;
import com.openclassrooms.model.Subscription;
import com.openclassrooms.service.SubscriptionService;
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
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private javax.persistence.EntityManager entityManager;

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserSubscriptions() {
        try {
            // Récupération de l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative d'accès aux abonnements sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour voir vos abonnements");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Récupération de l'ID de l'utilisateur depuis le token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Récupération des abonnements pour l'utilisateur: {}", userId);
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
            
            // Convertir les entités en DTOs
            List<SubscriptionDto> subscriptionDtos = subscriptions.stream()
                .map(SubscriptionDto::fromEntity)
                .collect(Collectors.toList());
                
            logger.info("Abonnements convertis en DTOs: {}", subscriptionDtos.size());
            return ResponseEntity.ok(subscriptionDtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des abonnements: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList()); 
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long userId) {
        try {
            // Vérification que l'utilisateur est authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("Tentative d'accès aux abonnements sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour voir les abonnements");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Récupération des abonnements pour l'utilisateur: {}", userId);
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
            
            // Convertir les entités en DTOs
            List<SubscriptionDto> subscriptionDtos = subscriptions.stream()
                .map(SubscriptionDto::fromEntity)
                .collect(Collectors.toList());
                
            logger.info("Abonnements convertis en DTOs: {}", subscriptionDtos.size());
            return ResponseEntity.ok(subscriptionDtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des abonnements: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList()); 
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToTheme(@RequestParam Long themeId) {
        try {
            // Récupération de l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative d'abonnement sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour vous abonner");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Récupération de l'ID de l'utilisateur depuis le token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Tentative d'abonnement: userId={}, themeId={}", userId, themeId);
            Subscription subscription = subscriptionService.subscribeToTheme(userId, themeId);
            logger.info("Abonnement réussi: {}", subscription);
            return ResponseEntity.ok(SubscriptionDto.fromEntity(subscription));
        } catch (RuntimeException e) {
            logger.error("Erreur lors de l'abonnement: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeFromTheme(@RequestParam Long themeId) {
        try {
            // Récupération de l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                !(authentication.getPrincipal() instanceof UserDetails)) {
                logger.error("Tentative de désabonnement sans authentification valide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous devez être connecté pour vous désabonner");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Récupération de l'ID de l'utilisateur depuis le token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            Long userId = userService.getUserIdByEmail(email);
            
            if (userId == null) {
                logger.error("Utilisateur avec l'email {} non trouvé en base", email);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            logger.info("Tentative de désabonnement: userId={}, themeId={}", userId, themeId);
            subscriptionService.unsubscribeFromTheme(userId, themeId);
            logger.info("Désabonnement réussi");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Erreur lors du désabonnement: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/debug/all")
    public ResponseEntity<?> getAllSubscriptions() {
        try {
            logger.info("Demande de récupération de tous les abonnements (débogage)");
            List<Subscription> allSubscriptions = subscriptionService.getAllSubscriptions();
            
            if (allSubscriptions.isEmpty()) {
                logger.warn("Aucun abonnement trouvé dans la base de données");
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            List<SubscriptionDto> dtos = allSubscriptions.stream()
                .map(sub -> {
                    try {
                        return SubscriptionDto.fromEntity(sub);
                    } catch (Exception e) {
                        logger.error("Erreur lors de la conversion de l'abonnement (ID: {}): {}", 
                            sub.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
            
            logger.info("Retour de {} abonnements", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les abonnements: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/debug/structure")
    public ResponseEntity<?> checkSubscriptionStructure() {
        try {
            logger.info("Vérification de la structure de la table subscriptions");
            
            String sql = "SHOW COLUMNS FROM subscriptions";
            List<?> resultList = entityManager.createNativeQuery(sql).getResultList();
            
            List<Map<String, String>> columns = new ArrayList<>();
            for (Object row : resultList) {
                Map<String, String> columnInfo = new HashMap<>();
                if (row instanceof Object[]) {
                    Object[] fields = (Object[]) row;
                    if (fields.length > 0) columnInfo.put("Field", fields[0] != null ? fields[0].toString() : "null");
                    if (fields.length > 1) columnInfo.put("Type", fields[1] != null ? fields[1].toString() : "null");
                    if (fields.length > 2) columnInfo.put("Null", fields[2] != null ? fields[2].toString() : "null");
                    if (fields.length > 3) columnInfo.put("Key", fields[3] != null ? fields[3].toString() : "null");
                }
                columns.add(columnInfo);
            }
            
            // Vérifier la clé primaire
            String pkSql = "SHOW KEYS FROM subscriptions WHERE Key_name = 'PRIMARY'";
            List<?> pkResultList = entityManager.createNativeQuery(pkSql).getResultList();
            List<String> primaryKeys = new ArrayList<>();
            for (Object row : pkResultList) {
                if (row instanceof Object[]) {
                    Object[] fields = (Object[]) row;
                    if (fields.length > 4) {
                        primaryKeys.add(fields[4].toString());
                    }
                }
            }
            
            // Vérifier les données
            String dataSql = "SELECT * FROM subscriptions LIMIT 5";
            List<?> dataResultList = entityManager.createNativeQuery(dataSql).getResultList();
            List<Map<String, String>> data = new ArrayList<>();
            for (Object row : dataResultList) {
                Map<String, String> rowData = new HashMap<>();
                if (row instanceof Object[]) {
                    Object[] fields = (Object[]) row;
                    for (int i = 0; i < fields.length; i++) {
                        rowData.put("Column_" + i, fields[i] != null ? fields[i].toString() : "null");
                    }
                }
                data.add(rowData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("columns", columns);
            result.put("primaryKeys", primaryKeys);
            result.put("sampleData", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de la structure: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
} 