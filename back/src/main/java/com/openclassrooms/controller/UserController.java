package com.openclassrooms.controller;

import com.openclassrooms.model.User;
import com.openclassrooms.security.JwtTokenProvider;
import com.openclassrooms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            logger.info("Tentative de connexion avec email: {}", loginRequest.get("email"));
            
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                logger.warn("Email ou mot de passe manquant ou vide");
                Map<String, String> response = new HashMap<>();
                response.put("message", "Email et mot de passe requis");
                return ResponseEntity.badRequest().body(response);
            }
// -->
            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.trim(), password)
                );
                Optional<User> userOpt = userService.getUserByEmail(email.trim());
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("token", token);
                    
                    logger.info("Connexion réussie pour l'utilisateur: {}", user.getUsername());
                    return ResponseEntity.ok(response);
                } else {
                    // Ce cas ne devrait pas se produire car l'authentification a réussi
                    logger.error("Utilisateur authentifié mais non trouvé en base: {}", email);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Erreur interne du serveur");
                    return ResponseEntity.status(500).body(response);
                }
                
            } catch (BadCredentialsException e) {
                logger.warn("Authentification échouée pour l'email: {}", email);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Email ou mot de passe incorrect");
                return ResponseEntity.badRequest().body(response);
            } catch (DataAccessException e) {
                logger.error("Erreur d'accès à la base de données: {}", e.getMessage(), e);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Problème de connexion à la base de données");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de la connexion");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            logger.info("Données d'inscription reçues: username={}, email={}, password={}",
                user.getUsername(),
                user.getEmail(),
                user.getPassword() != null ? "***" : "null");
    
            boolean usernameEmpty = user.getUsername() == null || user.getUsername().trim().isEmpty();
            boolean emailEmpty = user.getEmail() == null || user.getEmail().trim().isEmpty();
            boolean passwordEmpty = user.getPassword() == null || user.getPassword().trim().isEmpty();
            
            if (usernameEmpty || emailEmpty || passwordEmpty) {
                logger.warn("Données d'inscription incomplètes: username={}, email={}, password={}",
                    usernameEmpty ? "manquant" : "présent",
                    emailEmpty ? "manquant" : "présent",
                    passwordEmpty ? "manquant" : "présent");
                
                Map<String, String> response = new HashMap<>();
                
                if (usernameEmpty) {
                    response.put("message", "Le nom d'utilisateur est requis");
                } else if (emailEmpty) {
                    response.put("message", "L'email est requis");
                } else if (passwordEmpty) {
                    response.put("message", "Le mot de passe est requis");
                } else {
                    response.put("message", "Tous les champs sont requis");
                }
                
                return ResponseEntity.badRequest().body(response);
            }
            user.setUsername(user.getUsername().trim());
            user.setEmail(user.getEmail().trim());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
    
            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                logger.warn("Tentative d'inscription avec un email déjà utilisé: {}", user.getEmail());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Cet email est déjà utilisé");
                return ResponseEntity.badRequest().body(response);
            }

            if (userService.getUserByUsername(user.getUsername()).isPresent()) {
                logger.warn("Tentative d'inscription avec un nom d'utilisateur déjà utilisé: {}", user.getUsername());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Ce nom d'utilisateur est déjà utilisé");
                return ResponseEntity.badRequest().body(response);
            }
            User createdUser = userService.createUser(user);
            logger.info("Inscription réussie pour l'utilisateur: {}", createdUser.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Inscription réussie");
            response.put("userId", createdUser.getId().toString());
            return ResponseEntity.ok(response);
            
        } catch (DataIntegrityViolationException e) {
            logger.error("Violation de contrainte lors de l'inscription: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            
            if (e.getMessage().contains("username")) {
                response.put("message", "Ce nom d'utilisateur est déjà utilisé");
            } else if (e.getMessage().contains("email")) {
                response.put("message", "Cet email est déjà utilisé");
            } else {
                response.put("message", "Erreur de validation des données");
            }
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (RuntimeException e) {
            logger.error("Erreur lors de l'inscription: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'inscription: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Une erreur est survenue lors de l'inscription");
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/test/create-default-user")
    public ResponseEntity<?> createDefaultUser() {
        try {
            String defaultEmail = "user1@example.com";
            Optional<User> existingUser = userService.getUserByEmail(defaultEmail);
            
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setPassword(passwordEncoder.encode("password123"));
                user = userService.updateUser(user.getId(), user);
                logger.info("Utilisateur par défaut mis à jour: {}", user.getUsername());
            } else {
                user = new User();
                user.setUsername("user1");
                user.setEmail(defaultEmail);
                user.setPassword(passwordEncoder.encode("password123"));
                user = userService.createUser(user);
                logger.info("Utilisateur par défaut créé: {}", user.getUsername());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur par défaut créé/mis à jour avec succès");
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("password", "password123");
            
            response.put("user", userMap);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur par défaut: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de la création de l'utilisateur par défaut");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 