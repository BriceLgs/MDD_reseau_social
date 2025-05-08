package com.openclassrooms.service;

import com.openclassrooms.model.Theme;
import com.openclassrooms.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class ThemeService {

    private static final Logger logger = LoggerFactory.getLogger(ThemeService.class);

    @Autowired
    private ThemeRepository themeRepository;

    public List<Theme> getAllThemes() {
        try {
            return themeRepository.findAll();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les thèmes", e);
            return new ArrayList<>();
        }
    }

    public Theme getThemeById(Long id) {
        try {
            return themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du thème " + id, e);
            throw new RuntimeException("Erreur lors de la récupération du thème", e);
        }
    }

    public Theme createTheme(Theme theme) {
        try {
            return themeRepository.save(theme);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du thème", e);
            throw new RuntimeException("Erreur lors de la création du thème", e);
        }
    }

    public Theme updateTheme(Long id, Theme themeDetails) {
        try {
            Theme theme = getThemeById(id);
            theme.setName(themeDetails.getName());
            theme.setDescription(themeDetails.getDescription());
            return themeRepository.save(theme);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du thème " + id, e);
            throw new RuntimeException("Erreur lors de la mise à jour du thème", e);
        }
    }

    public void deleteTheme(Long id) {
        try {
            Theme theme = getThemeById(id);
            themeRepository.delete(theme);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du thème " + id, e);
            throw new RuntimeException("Erreur lors de la suppression du thème", e);
        }
    }
} 