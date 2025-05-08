package com.openclassrooms.repository;

import com.openclassrooms.model.Subscription;
import com.openclassrooms.model.Theme;
import com.openclassrooms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    List<Subscription> findByTheme(Theme theme);
    Optional<Subscription> findByUserAndTheme(User user, Theme theme);
    boolean existsByUserAndTheme(User user, Theme theme);
} 