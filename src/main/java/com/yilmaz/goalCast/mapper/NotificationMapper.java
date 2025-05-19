package com.yilmaz.goalCast.mapper;

import com.yilmaz.goalCast.dto.notification.NotificationDto;
import com.yilmaz.goalCast.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        // BaseEntity'den createdAt geliyorsa:
        if (notification.getCreatedAt() != null) {
             dto.setCreatedAt(notification.getCreatedAt());
        }
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setLink(notification.getLink());
        return dto;
    }

    // toEntity veya updateEntityFromDto metotları şu an için gerekmeyebilir,
    // çünkü bildirimler genellikle sistem tarafından oluşturulur.
}