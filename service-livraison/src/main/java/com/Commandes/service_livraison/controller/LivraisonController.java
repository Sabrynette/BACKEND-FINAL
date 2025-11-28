package com.Commandes.service_livraison.controller;


import com.Commandes.service_livraison.dto.LivraisonDTO;
import com.Commandes.service_livraison.service.LivraisonService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livraisons")
public class LivraisonController {

    private static final Logger logger = LoggerFactory.getLogger(LivraisonController.class);
    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    @PostMapping
    public ResponseEntity<LivraisonDTO> createLivraison(@Valid @RequestBody LivraisonDTO livraisonDTO) {
        logger.debug("Requête POST reçue avec LivraisonDTO : {}", livraisonDTO);
        LivraisonDTO savedLivraison = livraisonService.createLivraison(livraisonDTO);
        return new ResponseEntity<>(savedLivraison, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivraisonDTO> updateLivraison(@PathVariable Long id, @Valid @RequestBody LivraisonDTO livraisonDTO) {
        LivraisonDTO updatedLivraison = livraisonService.updateLivraison(id, livraisonDTO);
        return new ResponseEntity<>(updatedLivraison, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraisonById(@PathVariable Long id) {
        LivraisonDTO livraison = livraisonService.getLivraisonById(id);
        return new ResponseEntity<>(livraison, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LivraisonDTO>> getAllLivraisons() {
        List<LivraisonDTO> livraisons = livraisonService.getAllLivraisons();
        return new ResponseEntity<>(livraisons, HttpStatus.OK);
    }
}
