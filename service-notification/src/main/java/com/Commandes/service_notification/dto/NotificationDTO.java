package com.Commandes.service_notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Message is required")
    private String message;

    @NotNull(message = "Type is required")
    private String type;

    private boolean read; // Renommé de 'isRead' à 'read' pour une meilleure compatibilité Lombok

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Méthode d'accès spéciale pour la propriété booléenne
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}