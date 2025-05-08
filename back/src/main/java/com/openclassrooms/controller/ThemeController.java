package com.openclassrooms.controller;

import com.openclassrooms.model.Theme;
import com.openclassrooms.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/themes")
@CrossOrigin(origins = "http://localhost:4200")
public class ThemeController {

    private static final Logger logger = LoggerFactory.getLogger(ThemeController.class);

    @Autowired
    private ThemeService themeService;

    @GetMapping
    public ResponseEntity<List<Theme>> getAllThemes() {
        try {
            List<Theme> themes = themeService.getAllThemes();
            return ResponseEntity.ok(themes);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des thèmes", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable Long id) {
        try {
            Theme theme = themeService.getThemeById(id);
            return ResponseEntity.ok(theme);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération du thème " + id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Theme> createTheme(@RequestBody Theme theme) {
        try {
            Theme createdTheme = themeService.createTheme(theme);
            return ResponseEntity.ok(createdTheme);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la création du thème", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theme> updateTheme(@PathVariable Long id, @RequestBody Theme themeDetails) {
        try {
            Theme updatedTheme = themeService.updateTheme(id, themeDetails);
            return ResponseEntity.ok(updatedTheme);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la mise à jour du thème " + id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTheme(@PathVariable Long id) {
        try {
            themeService.deleteTheme(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la suppression du thème " + id, e);
            return ResponseEntity.notFound().build();
        }
    }
} 