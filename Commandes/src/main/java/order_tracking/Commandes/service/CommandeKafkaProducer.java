package order_tracking.Commandes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import order_tracking.Commandes.dto.CommandeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommandeKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(CommandeKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${topic.name.commande-created}") // à configurer dans application.yml
    private String topicName;

    public CommandeKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        // Enregistrement du module JavaTime pour gérer LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
        // Optionnel : pour éviter les erreurs de sérialisation des timestamps
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void sendCommandeCreatedEvent(CommandeDTO commandeDTO) {
        try {
            String message = objectMapper.writeValueAsString(commandeDTO);
            kafkaTemplate.send(topicName, message);
            log.info("✅ Événement Commande créée envoyé à Kafka : {}", message);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la conversion ou l'envoi de la commande à Kafka", e);
        }
    }
}
