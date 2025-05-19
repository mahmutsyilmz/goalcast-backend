package com.yilmaz.goalCast.dto.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnreadCountResponseDto {
    private long unreadCount;
    private int currentUserTotalPoints;
}