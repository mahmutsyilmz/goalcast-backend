package com.yilmaz.goalCast.dto.notification;

import com.yilmaz.goalCast.model.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {
    private Long id;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long relatedEntityId;
    private String link;
    // private Long userId;
    // private String username;
}