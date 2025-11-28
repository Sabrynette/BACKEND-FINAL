package com.Commandes.service_notification.service;

import com.Commandes.service_notification.dto.NotificationDTO;
import com.Commandes.service_notification.entity.Notification;
import com.Commandes.service_notification.entity.NotificationType;
import com.Commandes.service_notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        log.debug("Creating notification: {}", notificationDTO);
        Notification notification = toEntity(notificationDTO);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setRead(false);  // Changé de setIsRead à setRead
        Notification savedNotification = notificationRepository.save(notification);
        return toDTO(savedNotification);
    }

    public NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvée"));
        existingNotification.setUserId(notificationDTO.getUserId());
        existingNotification.setMessage(notificationDTO.getMessage());
        existingNotification.setType(NotificationType.valueOf(notificationDTO.getType()));
        existingNotification.setRead(notificationDTO.isRead());  // Changé de setIsRead à setRead
        existingNotification.setUpdatedAt(LocalDateTime.now());
        Notification updatedNotification = notificationRepository.save(existingNotification);
        return toDTO(updatedNotification);
    }

    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvée"));
        return toDTO(notification);
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private Notification toEntity(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setId(notificationDTO.getId());
        notification.setUserId(notificationDTO.getUserId());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(NotificationType.valueOf(notificationDTO.getType()));
        notification.setRead(notificationDTO.isRead());  // Changé de setIsRead à setRead
        notification.setCreatedAt(notificationDTO.getCreatedAt());
        notification.setUpdatedAt(notificationDTO.getUpdatedAt());
        return notification;
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setUserId(notification.getUserId());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setType(notification.getType() != null ? notification.getType().name() : null);
        notificationDTO.setRead(notification.isRead());  // Changé de setIsRead à setRead
        notificationDTO.setCreatedAt(notification.getCreatedAt());
        notificationDTO.setUpdatedAt(notification.getUpdatedAt());
        return notificationDTO;
    }
}