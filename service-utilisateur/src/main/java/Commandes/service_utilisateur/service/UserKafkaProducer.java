package Commandes.service_utilisateur.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(String userJson) {
        kafkaTemplate.send("user-created-topic", userJson);
        System.out.println("User created event sent to Kafka");
    }
}
