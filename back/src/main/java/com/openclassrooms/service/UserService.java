package com.openclassrooms.service;

import com.openclassrooms.model.User;
import com.openclassrooms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Recherche de l'utilisateur avec l'ID: {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        logger.debug("Recherche de l'utilisateur avec l'email: {}", email);
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                logger.debug("Utilisateur trouvé avec l'email: {}", email);
            } else {
                logger.debug("Aucun utilisateur trouvé avec l'email: {}", email);
            }
            return user;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par email: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<User> getUserByUsername(String username) {
        logger.debug("Recherche de l'utilisateur avec le nom d'utilisateur: {}", username);
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        logger.info("Création d'un nouvel utilisateur avec email: {} et nom d'utilisateur: {}", user.getEmail(), user.getUsername());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Tentative de création d'un utilisateur avec un email déjà existant: {}", user.getEmail());
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Tentative de création d'un utilisateur avec un nom d'utilisateur déjà existant: {}", user.getUsername());
            throw new RuntimeException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }
        
        try {
            User savedUser = userRepository.save(user);
            logger.info("Utilisateur créé avec succès, ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.getEmail().equals(userDetails.getEmail()) 
            && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        if (!user.getUsername().equals(userDetails.getUsername()) 
            && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        userRepository.delete(user);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }
} 