package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.user.LeaderboardEntryDto;
import com.yilmaz.goalCast.dto.user.UserManagementDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.model.User;

import java.util.List;

public interface UserService {

    UserProfileDto getProfile(String username);
    List<LeaderboardEntryDto> getLeaderboard(int limit);
    User getUserById(Long userId);
    List<UserManagementDto> getAllUsersForAdmin();
    UserManagementDto adjustUserPoints(Long userId, int pointsAdjustment);
    void deleteUserByAdmin(Long userId);
    UserManagementDto getUserDetailsForAdmin(Long userId);
}
