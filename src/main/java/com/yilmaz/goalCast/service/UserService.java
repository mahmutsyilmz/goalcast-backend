package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.user.UserProfileDto;

public interface UserService {

    UserProfileDto getProfile(String username);
}
