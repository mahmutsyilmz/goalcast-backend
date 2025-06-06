// src/main/java/com/yilmaz/goalCast/mapper/UserMapper.java
package com.yilmaz.goalCast.mapper;

import com.yilmaz.goalCast.dto.user.UserManagementDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setTotalPoints(user.getTotalPoints());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setEmailVerified(user.isEmailVerified());
        return dto;
    }

    public UserManagementDto toUserManagementDto(User user) {
        if (user == null) {
            return null;
        }
        UserManagementDto dto = new UserManagementDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setTotalPoints(user.getTotalPoints());
        dto.setRole(user.getRole());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());


        return dto;
    }



}
