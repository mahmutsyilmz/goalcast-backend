package com.yilmaz.goalCast.dto.user;

import com.yilmaz.goalCast.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {

    private Long id;
    private String username;
    private String email;
    private int totalPoints;
    private String role;

}
