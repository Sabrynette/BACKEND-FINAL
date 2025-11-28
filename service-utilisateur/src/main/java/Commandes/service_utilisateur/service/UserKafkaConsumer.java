package Commandes.service_utilisateur.service;

import org.springframework.kafka.annotation.KafkaListener;

public class UserKafkaConsumer {

    @KafkaListener(topics = "notification-topic", groupId = "users-group")
    public void consumeNotification(String message) {
        System.out.println("UserService received notification: " + message);
        // Implémente ici la logique à exécuter lors de la réception d'une notification
    }
}
