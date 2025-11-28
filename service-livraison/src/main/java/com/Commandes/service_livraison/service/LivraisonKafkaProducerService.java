package com.Commandes.service_livraison.service;

import org.springframework.kafka.core.KafkaTemplate;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

public class LivraisonKafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public LivraisonKafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
