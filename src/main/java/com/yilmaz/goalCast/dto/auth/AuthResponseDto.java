package com.yilmaz.goalCast.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
    private Long id;
}

