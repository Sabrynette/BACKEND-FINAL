package com.Commandes.service_livraison.service;

import com.Commandes.service_livraison.dto.LivraisonDTO;
import com.Commandes.service_livraison.entity.Livraison;
import com.Commandes.service_livraison.entity.LivraisonStatus;
import com.Commandes.service_livraison.repository.LivraisonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class LivraisonService {



    private final LivraisonRepository livraisonRepository;

    public LivraisonService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }

    public LivraisonDTO createLivraison(LivraisonDTO livraisonDTO) {
        Livraison livraison = toEntity(livraisonDTO);
        livraison.setCreatedAt(LocalDateTime.now());
        livraison.setUpdatedAt(LocalDateTime.now());
        Livraison savedLivraison = livraisonRepository.save(livraison);
        return toDTO(savedLivraison);
    }

    public LivraisonDTO updateLivraison(Long id, LivraisonDTO livraisonDTO) {
        Livraison existingLivraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
        existingLivraison.setDeliveryManId(livraisonDTO.getDeliveryManId());
        existingLivraison.setStatus(LivraisonStatus.valueOf(livraisonDTO.getStatus()));
        existingLivraison.setUpdatedAt(LocalDateTime.now());
        Livraison updatedLivraison = livraisonRepository.save(existingLivraison);
        return toDTO(updatedLivraison);
    }

    public LivraisonDTO getLivraisonById(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
        return toDTO(livraison);
    }

    public List<LivraisonDTO> getAllLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private Livraison toEntity(LivraisonDTO livraisonDTO) {
        Livraison livraison = new Livraison();
        livraison.setId(livraisonDTO.getId());
        livraison.setOrderId(livraisonDTO.getOrderId());
        livraison.setDeliveryManId(livraisonDTO.getDeliveryManId());

        if (livraisonDTO.getStatus() != null) {
            try {
                livraison.setStatus(LivraisonStatus.valueOf(livraisonDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
            }
        }

        livraison.setCreatedAt(livraisonDTO.getCreatedAt());
        livraison.setUpdatedAt(livraisonDTO.getUpdatedAt());
        return livraison;
    }

    private LivraisonDTO toDTO(Livraison livraison) {
        LivraisonDTO livraisonDTO = new LivraisonDTO();
        livraisonDTO.setId(livraison.getId());
        livraisonDTO.setOrderId(livraison.getOrderId());
        livraisonDTO.setDeliveryManId(livraison.getDeliveryManId());
        livraisonDTO.setStatus(livraison.getStatus() != null ? livraison.getStatus().name() : null);
        livraisonDTO.setCreatedAt(livraison.getCreatedAt());
        livraisonDTO.setUpdatedAt(livraison.getUpdatedAt());
        return livraisonDTO;
    }
}
