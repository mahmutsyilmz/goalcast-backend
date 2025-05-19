package com.yilmaz.goalCast.controller.user; // Paket adını kendi yapına göre ayarla

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.dto.notification.NotificationDto; // Yeni DTO oluşturacağız
import com.yilmaz.goalCast.dto.notification.UnreadCountResponseDto;
import com.yilmaz.goalCast.dto.notification.UserNotificationsPageResponseDto;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.security.CustomUserDetails;
import com.yilmaz.goalCast.service.UserNotificationService; // Yeni Servis oluşturacağız veya mevcut NotificationService'i genişleteceğiz

import com.yilmaz.goalCast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Sayfalama için
import org.springframework.data.domain.Pageable; // Sayfalama için
import org.springframework.data.web.PageableDefault; // Varsayılan sayfalama ayarları için
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final UserNotificationService userNotificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseDto<UserNotificationsPageResponseDto>> getUserNotifications(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
        User user = userService.getUserById(currentUser.getUser().getId());
        Page<NotificationDto> notificationPage = userNotificationService.getUserNotifications(currentUser.getUser().getId(), pageable);

        UserNotificationsPageResponseDto responseDto = new UserNotificationsPageResponseDto();
        responseDto.setContent(notificationPage.getContent());
        responseDto.setCurrentPage(notificationPage.getNumber());
        responseDto.setTotalItems(notificationPage.getTotalElements());
        responseDto.setTotalPages(notificationPage.getTotalPages());
        responseDto.setFirst(notificationPage.isFirst());
        responseDto.setLast(notificationPage.isLast());
        responseDto.setCurrentUserTotalPoints(user.getTotalPoints());

        return ResponseEntity.ok(new ResponseDto<>("Notifications fetched successfully.", true, responseDto));
    }

    // 2. Okunmamış bildirim sayısını getirme
    @GetMapping("/unread-count")
    public ResponseEntity<ResponseDto<UnreadCountResponseDto>> getUnreadNotificationCount(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        long count = userNotificationService.getUnreadNotificationCount(currentUser.getUser().getId());
        User user = userService.getUserById(currentUser.getUser().getId()); // Güncel kullanıcı
        UnreadCountResponseDto responseData = new UnreadCountResponseDto();
        responseData.setUnreadCount(count);
        responseData.setCurrentUserTotalPoints(user.getTotalPoints());
        return ResponseEntity.ok(new ResponseDto<>("Unread notification count fetched.", true, responseData));
    }

    // 3. Belirli bir bildirimi okundu olarak işaretleme
    @PostMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<ResponseDto<NotificationDto>> markNotificationAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long notificationId) {
        NotificationDto updatedNotification = userNotificationService.markNotificationAsRead(currentUser.getUser().getId(), notificationId);
        return ResponseEntity.ok(new ResponseDto<>("Notification marked as read.", true, updatedNotification));
    }

    // 4. Kullanıcının tüm okunmamış bildirimlerini okundu olarak işaretleme
    @PostMapping("/mark-all-as-read")
    public ResponseEntity<ResponseDto<Integer>> markAllNotificationsAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        int markedCount = userNotificationService.markAllNotificationsAsRead(currentUser.getUser().getId());
        return ResponseEntity.ok(new ResponseDto<>(markedCount + " notifications marked as read.", true, markedCount));
    }
}