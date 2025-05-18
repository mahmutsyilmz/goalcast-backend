package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.user.LeaderboardEntryDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;

import java.util.List;

public interface UserService {

    UserProfileDto getProfile(String username);
    List<LeaderboardEntryDto> getLeaderboard(int limit);
}
