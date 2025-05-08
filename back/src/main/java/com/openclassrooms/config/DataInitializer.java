package com.openclassrooms.config;

import com.openclassrooms.model.Theme;
import com.openclassrooms.model.User;
import com.openclassrooms.repository.ThemeRepository;
import com.openclassrooms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ThemeRepository themeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialiser les thèmes seulement si la table est vide
        if (themeRepository.count() == 0) {
            List<Theme> themesDefaut = Arrays.asList(
                createTheme("Angular", "Framework JavaScript pour le développement d'applications web"),
                createTheme("React", "Bibliothèque JavaScript pour créer des interfaces utilisateur"),
                createTheme("JavaScript", "Langage de programmation de scripts pour le web"),
                createTheme("HTML", "Langage de balisage pour les pages web"),
                createTheme("CSS", "Langage de style pour la présentation des pages web"),
                createTheme("Java", "Langage de programmation orienté objet")
            );
            
            themeRepository.saveAll(themesDefaut);
            System.out.println("Thèmes par défaut ajoutés avec succès");
        }
        
        // Initialiser un utilisateur par défaut si la table est vide
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("user1");
            defaultUser.setEmail("user1@example.com");
            defaultUser.setPassword(passwordEncoder.encode("password123"));
            defaultUser.setDateCreation(LocalDateTime.now());
            
            userRepository.save(defaultUser);
            System.out.println("Utilisateur par défaut ajouté avec succès");
        }
        
        // Mettre à jour les mots de passe non encodés des utilisateurs existants
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Vérifier si le mot de passe est déjà encodé (commence généralement par $2a$)
            if (!user.getPassword().startsWith("$2a$")) {
                System.out.println("Mise à jour du mot de passe non encodé pour l'utilisateur: " + user.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
            }
        }
    }
    
    private Theme createTheme(String name, String description) {
        Theme theme = new Theme();
        theme.setName(name);
        theme.setDescription(description);
        return theme;
    }
} 