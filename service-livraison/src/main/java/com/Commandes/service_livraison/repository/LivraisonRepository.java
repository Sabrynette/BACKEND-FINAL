package com.Commandes.service_livraison.repository;

import com.Commandes.service_livraison.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
}
