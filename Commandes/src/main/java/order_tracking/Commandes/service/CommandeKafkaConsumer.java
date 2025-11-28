package order_tracking.Commandes.service;

import org.springframework.kafka.annotation.KafkaListener;

public class CommandeKafkaConsumer {

    @KafkaListener(topics = "notification-topic", groupId = "commandes-group")
    public void consumeNotification(String message) {
        System.out.println("Commande Service a reçu message du topic notification: " + message);
        // logique métier (ex: mise à jour état commande)
    }
}
