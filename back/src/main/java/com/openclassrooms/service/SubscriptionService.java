package com.openclassrooms.service;

import com.openclassrooms.model.Subscription;
import com.openclassrooms.model.Theme;
import com.openclassrooms.model.User;
import com.openclassrooms.repository.SubscriptionRepository;
import com.openclassrooms.repository.UserRepository;
import com.openclassrooms.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private javax.persistence.EntityManager entityManager;

    public List<Subscription> getUserSubscriptions(Long userId) {
        try {
            logger.info("Recherche des abonnements pour l'utilisateur: {}", userId);
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
            logger.info("Utilisateur trouvé: {}", user);
            
            // Recherche via le repository standard
            List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
            logger.info("Abonnements trouvés via repository (nombre): {}", subscriptions.size());
            
            // Tentative de recherche directe avec SQL natif
            try {
                String sql = "SELECT * FROM subscriptions WHERE user_id = :userId";
                List<?> resultList = entityManager.createNativeQuery(sql, Subscription.class)
                    .setParameter("userId", userId)
                    .getResultList();
                
                logger.info("Abonnements trouvés via SQL natif (nombre): {}", resultList.size());
                
                if (resultList.size() > 0 && subscriptions.isEmpty()) {
                    logger.warn("Incohérence détectée: SQL natif trouve des abonnements mais le repository non");
                    // Dans ce cas, essayons d'utiliser les résultats SQL
                    @SuppressWarnings("unchecked")
                    List<Subscription> sqlSubscriptions = (List<Subscription>) resultList;
                    subscriptions = sqlSubscriptions;
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la requête SQL native: {}", e.getMessage(), e);
            }
            
            // Logging détaillé de chaque abonnement
            if (!subscriptions.isEmpty()) {
                logger.info("Détails des abonnements:");
                for (Subscription sub : subscriptions) {
                    logger.info("Abonnement ID: {}, User ID: {}, Theme ID: {}, Theme Name: {}, Date: {}", 
                        sub.getId(), 
                        sub.getUser().getId(), 
                        sub.getTheme().getId(), 
                        sub.getTheme().getName(),
                        sub.getDateSubscription());
                }
            } else {
                logger.warn("Aucun abonnement trouvé pour l'utilisateur {}", userId);
                
                // Vérifier si des abonnements existent dans la base de données
                long count = subscriptionRepository.count();
                logger.info("Nombre total d'abonnements dans la base: {}", count);
                
                if (count > 0) {
                    logger.info("Liste de tous les abonnements dans la base:");
                    subscriptionRepository.findAll().forEach(sub -> {
                        logger.info("Abonnement en base - ID: {}, User ID: {}, Theme ID: {}", 
                            sub.getId(),
                            sub.getUser() != null ? sub.getUser().getId() : "null",
                            sub.getTheme() != null ? sub.getTheme().getId() : "null");
                    });
                }
            }
            
            return subscriptions;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des abonnements: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Subscription subscribeToTheme(Long userId, Long themeId) {
        try {
            logger.info("Tentative d'abonnement: userId={}, themeId={}", userId, themeId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
            logger.info("Utilisateur trouvé: {}", user);
            
            Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Thème non trouvé avec ID: " + themeId));
            logger.info("Thème trouvé: {}", theme);

            if (subscriptionRepository.existsByUserAndTheme(user, theme)) {
                logger.warn("L'utilisateur {} est déjà abonné au thème {}", userId, themeId);
                throw new RuntimeException("L'utilisateur est déjà abonné à ce thème");
            }

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setTheme(theme);
            subscription.setDateSubscription(LocalDateTime.now());
            
            logger.info("Sauvegarde de l'abonnement: {}", subscription);
            Subscription savedSubscription = subscriptionRepository.save(subscription);
            logger.info("Abonnement sauvegardé avec succès: {}", savedSubscription);
            
            return savedSubscription;
        } catch (Exception e) {
            logger.error("Erreur lors de l'abonnement: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void unsubscribeFromTheme(Long userId, Long themeId) {
        try {
            logger.info("Tentative de désabonnement: userId={}, themeId={}", userId, themeId);
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID: " + userId));
            logger.info("Utilisateur trouvé: {}", user);
            
            Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Thème non trouvé avec ID: " + themeId));
            logger.info("Thème trouvé: {}", theme);

            Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserAndTheme(user, theme);
            
            if (subscriptionOpt.isPresent()) {
                Subscription subscription = subscriptionOpt.get();
                logger.info("Suppression de l'abonnement: {}", subscription);
                subscriptionRepository.delete(subscription);
                logger.info("Abonnement supprimé avec succès");
            } else {
                logger.warn("Aucun abonnement trouvé pour userId={}, themeId={}", userId, themeId);
                throw new RuntimeException("Aucun abonnement trouvé pour cet utilisateur et ce thème");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du désabonnement: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère tous les abonnements de la base de données (pour débogage)
     */
    public List<Subscription> getAllSubscriptions() {
        try {
            logger.info("Récupération de tous les abonnements");
            List<Subscription> allSubscriptions = subscriptionRepository.findAll();
            logger.info("Nombre total d'abonnements: {}", allSubscriptions.size());
            return allSubscriptions;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les abonnements: {}", e.getMessage(), e);
            throw e;
        }
    }
} 