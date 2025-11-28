package com.Commandes.service_livraison.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LivraisonKafkaConsumerService {

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("Message reçu dans Notification : " + message);
        // Traiter l'événement notification ici
    }
}
