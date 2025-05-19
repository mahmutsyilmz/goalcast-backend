package com.yilmaz.goalCast.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
    private Long id;
    private int totalPoints;
    private boolean emailVerified;
}

