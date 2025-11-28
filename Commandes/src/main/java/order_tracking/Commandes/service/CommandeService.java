package order_tracking.Commandes.service;


import lombok.extern.slf4j.Slf4j;
import order_tracking.Commandes.dto.CommandeDTO;
import order_tracking.Commandes.entity.Commande;
import order_tracking.Commandes.entity.CommandeStatus;
import order_tracking.Commandes.repository.CommandeRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@Configuration
public class CommandeService {

    private static final Logger logger = LoggerFactory.getLogger(CommandeService.class);
    private final CommandeRepository commandeRepository;
    private final CommandeKafkaProducer commandeKafkaProducer;

    public CommandeService(CommandeRepository commandeRepository,CommandeKafkaProducer commandeKafkaProducer) {
        this.commandeRepository = commandeRepository;
        this.commandeKafkaProducer = commandeKafkaProducer;
    }

    public CommandeDTO createCommande(CommandeDTO commandeDTO) {
        logger.info("Création d'une nouvelle commande pour l'ID client : {}", commandeDTO.getCustomerId());
        Commande commande = mapToEntity(commandeDTO);
        Commande savedCommande = commandeRepository.save(commande);
        CommandeDTO savedDTO = mapToDTO(savedCommande);
        commandeKafkaProducer.sendCommandeCreatedEvent(savedDTO);
        return savedDTO;
    }

    public CommandeDTO getCommandeById(Long id) {
        logger.debug("Récupération de la commande avec l'ID : {}", id);
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id));
        return mapToDTO(commande);
    }

    public List<CommandeDTO> getAllCommandes() {
        logger.debug("Récupération de toutes les commandes");
        return commandeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CommandeDTO updateCommande(Long id, CommandeDTO commandeDTO) {
        logger.info("Mise à jour de la commande avec l'ID : {}", id);
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id));
        updateEntity(existingCommande, commandeDTO);
        Commande updatedCommande = commandeRepository.save(existingCommande);
        return mapToDTO(updatedCommande);
    }

    public void deleteCommande(Long id) {
        logger.info("Suppression de la commande avec l'ID : {}", id);
        if (!commandeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id);
        }
        commandeRepository.deleteById(id);
    }

    private CommandeDTO mapToDTO(Commande commande) {
        CommandeDTO dto = new CommandeDTO();
        dto.setId(commande.getId());
        dto.setCustomerId(commande.getCustomerId());
        dto.setOrderDate(commande.getOrderDate());
        dto.setStatus(commande.getStatus());
        dto.setTotalAmount(commande.getTotalAmount());
        return dto;
    }

    private Commande mapToEntity(CommandeDTO dto) {
        Commande commande = new Commande();
        commande.setId(dto.getId());
        commande.setCustomerId(dto.getCustomerId());
        commande.setOrderDate(dto.getOrderDate());
        commande.setStatus(dto.getStatus());
        commande.setTotalAmount(dto.getTotalAmount());
        return commande;
    }

    private void updateEntity(Commande commande, CommandeDTO dto) {
        commande.setCustomerId(dto.getCustomerId());
        commande.setOrderDate(dto.getOrderDate());
        commande.setStatus(dto.getStatus());
        commande.setTotalAmount(dto.getTotalAmount());
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }


    public Map<String, Integer> getCommandeStats() {
        List<Commande> commandes = commandeRepository.findAll();

        int total = commandes.size();
        int livrees = (int) commandes.stream().filter(c -> c.getStatus() == CommandeStatus.DELIVERED).count();
        int confirmes = (int) commandes.stream().filter(c -> c.getStatus() == CommandeStatus.CONFIRMED).count();
        int enCours = (int) commandes.stream().filter(c -> c.getStatus() == CommandeStatus.PENDING).count();
        int annulees = (int) commandes.stream().filter(c -> c.getStatus() == CommandeStatus.CANCELLED).count();

        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("enCours", enCours);       // ✅ correct
        stats.put("confirmes", confirmes);   // ✅ nouveau champ
        stats.put("livrees", livrees);
        stats.put("annulees", annulees);

        return stats;
    }




    public List<Integer> getWeeklyCommandes() {
        List<Commande> commandes = commandeRepository.findAll();

        // Tableau des 7 jours de la semaine (Lundi = 0, Dimanche = 6)
        int[] commandesParJour = new int[7];

        for (Commande commande : commandes) {
            int dayOfWeek = commande.getOrderDate().getDayOfWeek().getValue(); // 1=Lundi ... 7=Dimanche
            commandesParJour[dayOfWeek - 1]++; // lundi=0, dimanche=6

        }

        return Arrays.stream(commandesParJour).boxed().collect(Collectors.toList());
    }


}

