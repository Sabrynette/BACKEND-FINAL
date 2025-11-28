package order_tracking.Commandes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import order_tracking.Commandes.entity.CommandeStatus;


import java.time.LocalDateTime;



public class CommandeDTO {

    private Long id;

    @NotNull(message = "L'ID du client est requis")
    @Positive(message = "L'ID du client doit être positif")
    private Long customerId;

    @NotNull(message = "La date de la commande est requise")
    private LocalDateTime orderDate;

    @NotNull(message = "Le statut est requis")
    private CommandeStatus status;

    @NotNull(message = "Le montant total est requis")
    @Positive(message = "Le montant total doit être positif")
    private Double totalAmount;



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public CommandeStatus getStatus() {
        return status;
    }

    public void setStatus(CommandeStatus status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
