package com.Commandes.service_notification.service;

import org.springframework.kafka.core.KafkaTemplate;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

public class NotificationKafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public NotificationKafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
