package com.yilmaz.goalCast.dto.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserNotificationsPageResponseDto {
    private List<NotificationDto> content;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int currentUserTotalPoints;
}
