package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.notification.NotificationDto;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.NotificationMapper;
import com.yilmaz.goalCast.model.Notification;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.NotificationRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Lombok ile constructor injection
public class UserNotificationServiceImpl implements UserNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(UserNotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository; // User objesini almak için
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        logger.info("Fetching notifications for user: {}", user.getUsername());
        Page<Notification> notificationPage = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return notificationPage.map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        logger.info("Fetching unread notification count for user: {}", user.getUsername());
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public NotificationDto markNotificationAsRead(Long userId, Long notificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Kullanıcının kendi bildirimini mi okumaya çalıştığını kontrol et
        if (!notification.getUser().getId().equals(user.getId())) {
            // Normalde bu durumun oluşmaması lazım, çünkü kullanıcı sadece kendi bildirimlerini görmeli.
            // Ama güvenlik açısından bir kontrol. Farklı bir exception fırlatılabilir (örn: ForbiddenAccessException)
            logger.warn("User {} attempted to mark notification {} as read, but it does not belong to them.",
                    user.getUsername(), notificationId);
            throw new SecurityException("User does not have permission to mark this notification as read.");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            Notification updatedNotification = notificationRepository.save(notification);
            logger.info("Notification (id:{}) marked as read for user: {}", notificationId, user.getUsername());
            return notificationMapper.toDto(updatedNotification);
        }
        logger.info("Notification (id:{}) was already read for user: {}", notificationId, user.getUsername());
        return notificationMapper.toDto(notification); // Zaten okunmuşsa mevcut halini dön
    }

    @Override
    @Transactional
    public int markAllNotificationsAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        logger.info("Marking all unread notifications as read for user: {}", user.getUsername());
        int markedCount = notificationRepository.markAllAsReadForUser(user);
        logger.info("{} notifications marked as read for user: {}", markedCount, user.getUsername());
        return markedCount;
    }
}