package com.openclassrooms.controller;

import com.openclassrooms.model.Subscription;
import com.openclassrooms.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/user/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Subscription> subscribeToSubject(
            @RequestParam Long userId,
            @RequestParam Long subjectId) {
        try {
            Subscription subscription = subscriptionService.subscribeToSubject(userId, subjectId);
            return ResponseEntity.ok(subscription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeFromSubject(
            @RequestParam Long userId,
            @RequestParam Long subjectId) {
        try {
            subscriptionService.unsubscribeFromSubject(userId, subjectId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 