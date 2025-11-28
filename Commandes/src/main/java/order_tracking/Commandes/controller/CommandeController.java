package order_tracking.Commandes.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import order_tracking.Commandes.dto.CommandeDTO;
import order_tracking.Commandes.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commandes")

public class CommandeController {


    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getCommandeStats() {
        Map<String, Integer> stats = commandeService.getCommandeStats();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/weekly")
    public ResponseEntity<List<Integer>> getWeeklyCommandes() {
        return ResponseEntity.ok(commandeService.getWeeklyCommandes());
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande( @RequestBody CommandeDTO commandeDTO) {
        CommandeDTO createdCommande = commandeService.createCommande(commandeDTO);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable Long id) {
        CommandeDTO commande = commandeService.getCommandeById(id);
        return ResponseEntity.ok(commande);
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<CommandeDTO> commandes = commandeService.getAllCommandes();
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(@PathVariable Long id, @Valid @RequestBody CommandeDTO commandeDTO) {
        CommandeDTO updatedCommande = commandeService.updateCommande(id, commandeDTO);
        return ResponseEntity.ok(updatedCommande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

}
