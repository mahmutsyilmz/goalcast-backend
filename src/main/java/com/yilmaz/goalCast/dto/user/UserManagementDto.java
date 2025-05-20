package com.yilmaz.goalCast.dto.user;

import com.yilmaz.goalCast.model.Role;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserManagementDto {
    private Long id;
    private String username;
    private String email;
    private int totalPoints;
    private Role role;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}