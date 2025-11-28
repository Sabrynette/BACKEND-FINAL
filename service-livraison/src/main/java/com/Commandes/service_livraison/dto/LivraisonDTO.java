package com.Commandes.service_livraison.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LivraisonDTO {


        private Long id;

        @NotNull(message = "Order ID is required")
        private Long orderId;

        private Long deliveryManId;

        @NotNull(message = "Status is required")
        @Pattern(regexp = "PENDING|ASSIGNED|IN_PROGRESS|DELIVERED|CANCELLED",
                message = "Status must be one of: PENDING, ASSIGNED, IN_PROGRESS, DELIVERED, CANCELLED")
        private String status;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
}
