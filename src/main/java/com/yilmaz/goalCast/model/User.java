package com.yilmaz.goalCast.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity{

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    private String email;

    private String password;

    private int totalPoints = 5000;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String profileImagePath;

    @Column(name = "email_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean emailVerified = false;

    private String emailVerificationToken;

    private LocalDateTime emailVerificationTokenExpiryDate;


}
