package com.openclassrooms.repository;

import com.openclassrooms.model.Subscription;
import com.openclassrooms.model.User;
import com.openclassrooms.model.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    List<Subscription> findBySujet(Sujet sujet);
    Optional<Subscription> findByUserAndSujet(User user, Sujet sujet);
    boolean existsByUserAndSujet(User user, Sujet sujet);
} 