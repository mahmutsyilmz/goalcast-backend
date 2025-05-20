package com.yilmaz.goalCast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    private Long relatedEntityId;

    @Column(length = 255)
    private String link;

    public Notification(User user, String message, NotificationType type, String link, Long relatedEntityId) {
        this.user = user;
        this.message = message;
        this.type = type;
        this.link = link;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false; // Varsayılan
    }

}