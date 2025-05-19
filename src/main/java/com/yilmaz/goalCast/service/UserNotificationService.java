package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.notification.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserNotificationService {
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);
    long getUnreadNotificationCount(Long userId);
    NotificationDto markNotificationAsRead(Long userId, Long notificationId);
    int markAllNotificationsAsRead(Long userId);
    // Bu servis, NotificationEventListener'ın DB'ye yazdığı bildirimleri okuyacak.
    // NotificationEventListener'ın Notification oluşturma mantığı UserNotificationService'e de taşınabilir,
    // ama şu anki ayrım da kabul edilebilir.
}