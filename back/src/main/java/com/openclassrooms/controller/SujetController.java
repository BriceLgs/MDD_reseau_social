package com.openclassrooms.controller;

import com.openclassrooms.model.Sujet;
import com.openclassrooms.service.SujetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sujets")
public class SujetController {

    @Autowired
    private SujetService sujetService;

    @GetMapping
    public List<Sujet> getAllSujets() {
        return sujetService.getAllSujets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sujet> getSujetById(@PathVariable Long id) {
        return ResponseEntity.ok(sujetService.getSujetById(id));
    }

    @PostMapping
    public Sujet createSujet(@RequestBody Sujet sujet) {
        return sujetService.createSujet(sujet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sujet> updateSujet(@PathVariable Long id, @RequestBody Sujet sujetDetails) {
        try {
            Sujet updatedSujet = sujetService.updateSujet(id, sujetDetails);
            return ResponseEntity.ok(updatedSujet);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSujet(@PathVariable Long id) {
        try {
            sujetService.deleteSujet(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 