package com.yilmaz.goalCast.model;

import jakarta.persistence.*; // VEYA javax.persistence.*
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity { // BaseEntity'den id ve createdAt alabilir

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type; // Enum oluşturacağız

    @Column(nullable = false)
    private boolean isRead = false;

    private Long relatedEntityId; // İlgili maçın ID'si, tahminin ID'si vb.

    @Column(length = 255)
    private String link; // Bildirime tıklandığında gidilecek frontend linki

    public Notification(User user, String message, NotificationType type, String link, Long relatedEntityId) {
        this.user = user;
        this.message = message;
        this.type = type;
        this.link = link;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false; // Varsayılan
    }

}