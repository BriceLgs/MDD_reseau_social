package com.openclassrooms.service;

import com.openclassrooms.model.Sujet;
import com.openclassrooms.repository.SujetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SujetService {

    @Autowired
    private SujetRepository sujetRepository;

    public List<Sujet> getAllSujets() {
        return sujetRepository.findAll();
    }

    public Sujet getSujetById(Long id) {
        return sujetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sujet not found"));
    }

    public Sujet createSujet(Sujet sujet) {
        return sujetRepository.save(sujet);
    }

    public Sujet updateSujet(Long id, Sujet sujetDetails) {
        Sujet sujet = getSujetById(id);
        sujet.setName(sujetDetails.getName());
        sujet.setDescription(sujetDetails.getDescription());
        return sujetRepository.save(sujet);
    }

    public void deleteSujet(Long id) {
        Sujet sujet = getSujetById(id);
        sujetRepository.delete(sujet);
    }
} 