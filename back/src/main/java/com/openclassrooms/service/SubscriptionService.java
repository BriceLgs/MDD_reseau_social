package com.openclassrooms.service;

import com.openclassrooms.model.Subscription;
import com.openclassrooms.model.User;
import com.openclassrooms.model.Sujet;
import com.openclassrooms.repository.SubscriptionRepository;
import com.openclassrooms.repository.UserRepository;
import com.openclassrooms.repository.SujetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SujetRepository sujetRepository;

    public List<Subscription> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return subscriptionRepository.findByUser(user);
    }

    public Subscription subscribeToSubject(Long userId, Long sujetId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Sujet sujet = sujetRepository.findById(sujetId)
            .orElseThrow(() -> new RuntimeException("Sujet non trouvé"));

        if (subscriptionRepository.existsByUserAndSujet(user, sujet)) {
            throw new RuntimeException("Abonnement déjà existant");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSujet(sujet);
        return subscriptionRepository.save(subscription);
    }

    public void unsubscribeFromSubject(Long userId, Long sujetId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Sujet sujet = sujetRepository.findById(sujetId)
            .orElseThrow(() -> new RuntimeException("Sujet non trouvé"));

        subscriptionRepository.findByUserAndSujet(user, sujet)
            .ifPresent(subscription -> subscriptionRepository.delete(subscription));
    }
} 